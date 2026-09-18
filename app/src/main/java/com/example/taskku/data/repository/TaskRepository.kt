package com.example.taskku.data.repository

import androidx.room.withTransaction
import android.content.Context
import com.example.taskku.data.local.database.AppDatabase
import com.example.taskku.data.local.dao.*
import com.example.taskku.data.local.entity.*
import com.example.taskku.domain.model.*
import com.example.taskku.widget.TaskKuWidgetHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface TaskRepository {
    suspend fun insertTaskWithDetails(task: Task, members: List<Member>, subtasks: List<Subtask>, attachments: List<Attachment>): Long
    suspend fun updateTaskWithDetails(task: Task, members: List<Member>, subtasks: List<Subtask>, attachments: List<Attachment>)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: Long)
    suspend fun deleteTasksByIds(taskIds: List<Long>)
    suspend fun updateTaskStatus(taskId: Long, statusId: Long)
    suspend fun toggleSubtaskCompletion(subtaskId: Long, isCompleted: Boolean)
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    fun getTasksBySubject(subject: String): Flow<List<Task>>
    fun getTasksByStatus(statusId: Long): Flow<List<Task>>
    fun getUpcomingDeadlines(fromDate: Long, toDate: Long): Flow<List<Task>>
    fun getOverdueTasks(currentDate: Long): Flow<List<Task>>
    fun getTaskCountByStatus(): Flow<List<StatusCount>>
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val memberDao: MemberDao,
    private val subtaskDao: SubtaskDao,
    private val attachmentDao: AttachmentDao,
    private val database: AppDatabase? = null,
    private val context: Context? = null
) : TaskRepository {

    private fun TaskWithDetails.toDomainModel(): Task {
        return Task(
            id = task.id,
            title = task.title,
            description = task.description,
            subject = task.subject,
            type = TaskType.fromString(task.type),
            difficulty = Difficulty.fromString(task.difficulty),
            statusId = task.statusId,
            statusName = status?.name ?: "Belum Dikerjakan",
            statusColorHex = status?.colorHex ?: "#B2BEC3",
            deadlineDate = task.deadlineDate,
            deadlineTime = task.deadlineTime,
            groupName = task.groupName,
            notificationEnabled = task.notificationEnabled,
            reminderOffset = ReminderOffset.fromString(task.reminderOffset),
            tag = com.example.taskku.domain.model.TaskTag.fromString(task.tag),
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            members = members.map { Member(it.id, it.taskId, it.name) },
            subtasks = subtasks.map { Subtask(it.id, it.taskId, it.title, it.assignedMemberId, it.isCompleted) },
            attachments = attachments.map { Attachment(it.id, it.taskId, it.fileName, it.filePath, it.fileType, it.fileSize, it.addedAt) }
        )
    }

    private fun Task.toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = description,
            subject = subject,
            type = type.name,
            difficulty = difficulty.name,
            statusId = statusId,
            deadlineDate = deadlineDate,
            deadlineTime = deadlineTime,
            groupName = groupName,
            notificationEnabled = notificationEnabled,
            reminderOffset = reminderOffset.name,
            tag = tag.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    override suspend fun insertTaskWithDetails(task: Task, members: List<Member>, subtasks: List<Subtask>, attachments: List<Attachment>): Long = withContext(Dispatchers.IO) {
        val insertAction: suspend () -> Long = {
            val taskId = taskDao.insertTask(task.toEntity())
            
            val insertedMemberIds = if (members.isNotEmpty()) {
                memberDao.insertMembers(members.map { MemberEntity(0, taskId, it.name) })
            } else emptyList()

            if (subtasks.isNotEmpty()) {
                val entities = subtasks.map { st ->
                    val assignedId = if (st.assignedMemberIndex != null && st.assignedMemberIndex in insertedMemberIds.indices) {
                        insertedMemberIds[st.assignedMemberIndex]
                    } else {
                        st.assignedMemberId
                    }
                    SubtaskEntity(0, taskId, st.title, assignedId, st.isCompleted)
                }
                subtaskDao.insertSubtasks(entities)
            }
            if (attachments.isNotEmpty()) {
                attachmentDao.insertAttachments(attachments.map { AttachmentEntity(0, taskId, it.fileName, it.filePath, it.fileType, it.fileSize, it.addedAt) })
            }
            context?.let { TaskKuWidgetHelper.updateWidget(it) }
            taskId
        }

        if (database != null) {
            database.withTransaction { insertAction() }
        } else {
            insertAction()
        }
    }

    override suspend fun updateTaskWithDetails(task: Task, members: List<Member>, subtasks: List<Subtask>, attachments: List<Attachment>) = withContext(Dispatchers.IO) {
        val updateAction: suspend () -> Unit = {
            taskDao.updateTask(task.toEntity().copy(updatedAt = System.currentTimeMillis()))
            
            memberDao.deleteMembersByTaskId(task.id)
            val insertedMemberIds = if (members.isNotEmpty()) {
                memberDao.insertMembers(members.map { MemberEntity(0, task.id, it.name) })
            } else emptyList()
            
            subtaskDao.deleteSubtasksByTaskId(task.id)
            if (subtasks.isNotEmpty()) {
                val entities = subtasks.map { st ->
                    val assignedId = if (st.assignedMemberIndex != null && st.assignedMemberIndex in insertedMemberIds.indices) {
                        insertedMemberIds[st.assignedMemberIndex]
                    } else {
                        st.assignedMemberId
                    }
                    SubtaskEntity(0, task.id, st.title, assignedId, st.isCompleted)
                }
                subtaskDao.insertSubtasks(entities)
            }
            
            val oldAttachments = attachmentDao.getAttachmentsByTaskId(task.id)
            val newPaths = attachments.map { it.filePath }.toSet()
            oldAttachments.filter { it.filePath !in newPaths }.forEach { oldAtt ->
                try {
                    val file = java.io.File(oldAtt.filePath)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            attachmentDao.deleteAttachmentsByTaskId(task.id)
            if (attachments.isNotEmpty()) {
                attachmentDao.insertAttachments(attachments.map { AttachmentEntity(0, task.id, it.fileName, it.filePath, it.fileType, it.fileSize, it.addedAt) })
            }
            context?.let { TaskKuWidgetHelper.updateWidget(it) }
        }

        if (database != null) {
            database.withTransaction { updateAction() }
        } else {
            updateAction()
        }
    }

    override suspend fun deleteTask(task: Task): Unit = withContext(Dispatchers.IO) {
        task.attachments.forEach { att ->
            try {
                val file = java.io.File(att.filePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        taskDao.deleteTask(task.toEntity())
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        Unit
    }

    override suspend fun deleteTaskById(taskId: Long): Unit = withContext(Dispatchers.IO) {
        val attachments = attachmentDao.getAttachmentsByTaskId(taskId)
        attachments.forEach { att ->
            try {
                val file = java.io.File(att.filePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        taskDao.deleteTaskById(taskId)
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        Unit
    }

    override suspend fun deleteTasksByIds(taskIds: List<Long>): Unit = withContext(Dispatchers.IO) {
        taskIds.forEach { taskId ->
            val attachments = attachmentDao.getAttachmentsByTaskId(taskId)
            attachments.forEach { att ->
                try {
                    val file = java.io.File(att.filePath)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (database != null) {
            database.withTransaction {
                taskDao.deleteTasksByIds(taskIds)
            }
        } else {
            taskDao.deleteTasksByIds(taskIds)
        }
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        Unit
    }

    override suspend fun updateTaskStatus(taskId: Long, statusId: Long): Unit = withContext(Dispatchers.IO) {
        taskDao.updateTaskStatus(taskId, statusId)
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        Unit
    }

    override suspend fun toggleSubtaskCompletion(subtaskId: Long, isCompleted: Boolean): Unit = withContext(Dispatchers.IO) {
        subtaskDao.toggleCompletion(subtaskId, isCompleted)
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        Unit
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return taskDao.getTaskById(id)
            .map { it?.toDomainModel() }
            .flowOn(Dispatchers.IO)
    }

    override fun getTasksBySubject(subject: String): Flow<List<Task>> {
        return taskDao.getTasksBySubject(subject)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTasksByStatus(statusId: Long): Flow<List<Task>> {
        return taskDao.getTasksByStatus(statusId)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getUpcomingDeadlines(fromDate: Long, toDate: Long): Flow<List<Task>> {
        return taskDao.getUpcomingDeadlines(fromDate, toDate)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getOverdueTasks(currentDate: Long): Flow<List<Task>> {
        return taskDao.getOverdueTasks(currentDate)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTaskCountByStatus(): Flow<List<StatusCount>> {
        return taskDao.getTaskCountByStatus()
            .flowOn(Dispatchers.IO)
    }
}
