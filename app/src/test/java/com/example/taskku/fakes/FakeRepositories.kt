package com.example.taskku.fakes

import com.example.taskku.data.local.dao.StatusCount
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTaskRepository : TaskRepository {
    val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow

    override fun getTaskById(id: Long): Flow<Task?> =
        tasksFlow.map { list -> list.find { it.id == id } }

    override fun getTasksBySubject(subject: String): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.subject == subject } }

    override fun getTasksByStatus(statusId: Long): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.statusId == statusId } }

    override fun getUpcomingDeadlines(fromDate: Long, toDate: Long): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.deadlineDate in fromDate..toDate } }

    override fun getOverdueTasks(currentDate: Long): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.deadlineDate < currentDate } }

    override fun getTaskCountByStatus(): Flow<List<StatusCount>> =
        tasksFlow.map { list ->
            list.groupBy { it.statusId }.map { (statusId, group) ->
                StatusCount(statusId, group.firstOrNull()?.statusName ?: "", group.size)
            }
        }

    override suspend fun insertTaskWithDetails(
        task: Task,
        members: List<Member>,
        subtasks: List<Subtask>,
        attachments: List<Attachment>
    ): Long {
        val id = if (task.id > 0) task.id else nextId++
        val newMembers = members.mapIndexed { idx, m -> m.copy(id = (idx + 1).toLong(), taskId = id) }
        val newSubtasks = subtasks.mapIndexed { idx, s ->
            val assignedId = if (s.assignedMemberIndex != null && s.assignedMemberIndex in newMembers.indices) {
                newMembers[s.assignedMemberIndex].id
            } else s.assignedMemberId
            s.copy(id = (idx + 1).toLong(), taskId = id, assignedMemberId = assignedId)
        }
        val newTask = task.copy(
            id = id,
            members = newMembers,
            subtasks = newSubtasks,
            attachments = attachments.mapIndexed { idx, a -> a.copy(id = (idx + 1).toLong(), taskId = id) }
        )
        tasksFlow.value = tasksFlow.value + newTask
        return id
    }

    override suspend fun updateTaskWithDetails(
        task: Task,
        members: List<Member>,
        subtasks: List<Subtask>,
        attachments: List<Attachment>
    ) {
        val newMembers = members.mapIndexed { idx, m -> m.copy(id = (idx + 1).toLong(), taskId = task.id) }
        val newSubtasks = subtasks.mapIndexed { idx, s ->
            val assignedId = if (s.assignedMemberIndex != null && s.assignedMemberIndex in newMembers.indices) {
                newMembers[s.assignedMemberIndex].id
            } else s.assignedMemberId
            s.copy(id = if (s.id > 0) s.id else (idx + 1).toLong(), taskId = task.id, assignedMemberId = assignedId)
        }
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == task.id) {
                task.copy(members = newMembers, subtasks = newSubtasks, attachments = attachments)
            } else it
        }
    }

    override suspend fun deleteTask(task: Task) {
        tasksFlow.value = tasksFlow.value.filter { it.id != task.id }
    }

    override suspend fun deleteTaskById(taskId: Long) {
        tasksFlow.value = tasksFlow.value.filter { it.id != taskId }
    }

    override suspend fun deleteTasksByIds(taskIds: List<Long>) {
        tasksFlow.value = tasksFlow.value.filter { !taskIds.contains(it.id) }
    }

    override suspend fun updateTaskStatus(taskId: Long, statusId: Long) {
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == taskId) it.copy(statusId = statusId) else it
        }
    }

    override suspend fun toggleSubtaskCompletion(subtaskId: Long, isCompleted: Boolean) {
        tasksFlow.value = tasksFlow.value.map { task ->
            task.copy(
                subtasks = task.subtasks.map { st ->
                    if (st.id == subtaskId) st.copy(isCompleted = isCompleted) else st
                }
            )
        }
    }
}

class FakeSubjectRepository : SubjectRepository {
    val subjectsFlow = MutableStateFlow<List<Subject>>(
        listOf(
            Subject(1, "Matematika", isPreset = true, isVisible = true),
            Subject(2, "Bahasa Indonesia", isPreset = true, isVisible = true),
            Subject(3, "Bahasa Inggris", isPreset = true, isVisible = true),
            Subject(4, "Fisika", isPreset = true, isVisible = true),
            Subject(5, "Kimia", isPreset = true, isVisible = false)
        )
    )
    private var nextId = 10L

    override fun getAllSubjects(): Flow<List<Subject>> = subjectsFlow

    override fun getVisibleSubjects(): Flow<List<Subject>> =
        subjectsFlow.map { list -> list.filter { it.isVisible } }

    override suspend fun insertSubject(subject: Subject) {
        val id = if (subject.id > 0) subject.id else nextId++
        subjectsFlow.value = subjectsFlow.value + subject.copy(id = id)
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectsFlow.value = subjectsFlow.value.map {
            if (it.id == subject.id) subject else it
        }
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectsFlow.value = subjectsFlow.value.filter { it.id != subject.id }
    }

    override suspend fun deleteSubjectById(id: Long) {
        subjectsFlow.value = subjectsFlow.value.filter { it.id != id }
    }

    override suspend fun updateVisibility(id: Long, isVisible: Boolean) {
        subjectsFlow.value = subjectsFlow.value.map {
            if (it.id == id) it.copy(isVisible = isVisible) else it
        }
    }
}

class FakeStatusRepository : StatusRepository {
    val statusesFlow = MutableStateFlow<List<Status>>(
        listOf(
            Status(1, "Belum Dikerjakan", "#B2BEC3", 1, isDefault = true),
            Status(2, "Sedang Dikerjakan", "#74B9FF", 2, isDefault = true),
            Status(3, "Selesai", "#55EFC4", 3, isDefault = true),
            Status(4, "Revisi", "#FDCB6E", 4, isDefault = false)
        )
    )
    private var nextId = 10L

    override fun getAllStatuses(): Flow<List<Status>> = statusesFlow

    override suspend fun insertStatus(status: Status) {
        val id = if (status.id > 0) status.id else nextId++
        statusesFlow.value = (statusesFlow.value + status.copy(id = id)).sortedBy { it.sortOrder }
    }

    override suspend fun updateStatus(status: Status) {
        statusesFlow.value = statusesFlow.value.map {
            if (it.id == status.id) status else it
        }.sortedBy { it.sortOrder }
    }

    override suspend fun deleteStatus(status: Status) {
        statusesFlow.value = statusesFlow.value.filter { it.id != status.id }
    }

    override suspend fun deleteStatusAndResetTasks(statusId: Long) {
        statusesFlow.value = statusesFlow.value.filter { it.id != statusId }
    }
}

class FakeTimetableRepository : com.example.taskku.data.repository.TimetableRepository {
    val timetablesFlow = MutableStateFlow<List<TimetableItem>>(emptyList())
    private var nextId = 1L

    override suspend fun insertTimetable(item: TimetableItem): Long {
        val id = if (item.id > 0) item.id else nextId++
        val newItem = item.copy(id = id)
        timetablesFlow.value = timetablesFlow.value + newItem
        return id
    }

    override suspend fun updateTimetable(item: TimetableItem) {
        timetablesFlow.value = timetablesFlow.value.map {
            if (it.id == item.id) item else it
        }
    }

    override suspend fun deleteTimetable(item: TimetableItem) {
        timetablesFlow.value = timetablesFlow.value.filter { it.id != item.id }
    }

    override suspend fun deleteTimetableById(id: Long) {
        timetablesFlow.value = timetablesFlow.value.filter { it.id != id }
    }

    override fun getAllTimetables(): Flow<List<TimetableItem>> = timetablesFlow

    override fun getTimetablesByDay(dayOfWeek: Int): Flow<List<TimetableItem>> =
        timetablesFlow.map { list -> list.filter { it.dayOfWeek == dayOfWeek } }

    override fun getTimetableById(id: Long): Flow<TimetableItem?> =
        timetablesFlow.map { list -> list.find { it.id == id } }

    override fun getUpcomingNextClass(): Flow<TimetableItem?> =
        timetablesFlow.map { list ->
            val currentDay = SchoolDay.currentDayOfWeek()
            val cal = java.util.Calendar.getInstance()
            val currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
            val currentMin = cal.get(java.util.Calendar.MINUTE)
            val currentTime = String.format(java.util.Locale.ROOT, "%02d:%02d", currentHour, currentMin)

            list.filter { it.dayOfWeek == currentDay }
                .sortedBy { it.startTime }
                .firstOrNull { it.endTime > currentTime }
        }
}
