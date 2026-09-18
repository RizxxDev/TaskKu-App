package com.example.taskku.ui.taskform

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.*
import com.example.taskku.notification.NotificationScheduler
import com.example.taskku.util.FileStorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskFormViewModel(
    private val taskId: Long?,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val appContext: Context? = null,
    private val initialSubject: String? = null,
    private val initialDeadlineDate: Long? = null,
    private val initialTag: TaskTag? = null
) : ViewModel() {

    @Immutable
    data class SubtaskFormItem(
        val id: Long = 0,
        val title: String = "",
        val assignedMemberIndex: Int? = null,
        val isCompleted: Boolean = false
    )

    @Immutable
    data class FormState(
        val title: String = "",
        val subject: String = "",
        val type: TaskType = TaskType.PRIBADI,
        val tag: TaskTag = TaskTag.PR,
        val description: String = "",
        val deadlineDate: Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
        }.timeInMillis,
        val deadlineTime: String = "23:59",
        val difficulty: Difficulty = Difficulty.SEDANG,
        val statusId: Long = 1L,
        val notificationEnabled: Boolean = true,
        val reminderOffset: ReminderOffset = ReminderOffset.ONE_HOUR_BEFORE,
        val groupName: String = "",
        val members: List<String> = emptyList(),
        val subtasks: List<SubtaskFormItem> = emptyList(),
        val attachments: List<Attachment> = emptyList(),
        val availableSubjects: List<Subject> = emptyList(),
        val availableStatuses: List<Status> = emptyList(),
        val isSaving: Boolean = false,
        val errors: Map<String, String> = emptyMap(),
        val isEditMode: Boolean = false,
        val isLoading: Boolean = true,
        val createdAt: Long = System.currentTimeMillis()
    )

    private val _formState = MutableStateFlow(
        FormState(
            isEditMode = taskId != null,
            subject = initialSubject ?: "",
            tag = initialTag ?: TaskTag.PR,
            deadlineDate = initialDeadlineDate ?: Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
            }.timeInMillis,
            deadlineTime = if (initialDeadlineDate != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = initialDeadlineDate }
                String.format(java.util.Locale.ROOT, "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            } else "23:59"
        )
    )
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val subjects = subjectRepository.getVisibleSubjects().first()
            val statuses = statusRepository.getAllStatuses().first()

            val defaultStatusId = statuses.find { it.isDefault }?.id ?: statuses.firstOrNull()?.id ?: 1L
            val defaultSubject = subjects.firstOrNull()?.name ?: "Matematika"

            if (taskId != null && taskId > 0) {
                val existingTask = taskRepository.getTaskById(taskId).first()
                if (existingTask != null) {
                    val memberNames = existingTask.members.map { it.name }
                    val subtaskItems = existingTask.subtasks.map { st ->
                        val memberIdx = if (st.assignedMemberId != null) {
                            val found = existingTask.members.indexOfFirst { it.id == st.assignedMemberId }
                            if (found != -1) found else null
                        } else null

                        SubtaskFormItem(
                            id = st.id,
                            title = st.title,
                            assignedMemberIndex = memberIdx,
                            isCompleted = st.isCompleted
                        )
                    }

                    _formState.value = FormState(
                        title = existingTask.title,
                        subject = existingTask.subject,
                        type = existingTask.type,
                        tag = existingTask.tag,
                        description = existingTask.description,
                        deadlineDate = existingTask.deadlineDate,
                        deadlineTime = existingTask.deadlineTime,
                        difficulty = existingTask.difficulty,
                        statusId = existingTask.statusId,
                        notificationEnabled = existingTask.notificationEnabled,
                        reminderOffset = existingTask.reminderOffset,
                        groupName = existingTask.groupName,
                        members = memberNames,
                        subtasks = subtaskItems,
                        attachments = existingTask.attachments,
                        availableSubjects = subjects,
                        availableStatuses = statuses,
                        createdAt = existingTask.createdAt,
                        isEditMode = true,
                        isLoading = false
                    )
                    return@launch
                }
            }

            _formState.value = _formState.value.copy(
                subject = initialSubject ?: defaultSubject,
                statusId = defaultStatusId,
                availableSubjects = subjects,
                availableStatuses = statuses,
                isLoading = false
            )
        }
    }

    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(
            title = title,
            errors = _formState.value.errors - "title"
        )
    }

    fun updateSubject(subject: String) {
        _formState.value = _formState.value.copy(
            subject = subject,
            errors = _formState.value.errors - "subject"
        )
    }

    fun addNewSubject(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            subjectRepository.insertSubject(Subject(name = trimmed, isPreset = false, isVisible = true))
            val updatedSubjects = subjectRepository.getVisibleSubjects().first()
            _formState.value = _formState.value.copy(
                availableSubjects = updatedSubjects,
                subject = trimmed
            )
        }
    }

    fun updateType(type: TaskType) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun updateTag(tag: TaskTag) {
        _formState.value = _formState.value.copy(tag = tag)
    }

    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun updateDeadlineDate(dateMillis: Long) {
        _formState.value = _formState.value.copy(
            deadlineDate = dateMillis,
            errors = _formState.value.errors - "deadline"
        )
    }

    fun updateDeadlineTime(timeStr: String) {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 23
        val min = parts.getOrNull(1)?.toIntOrNull() ?: 59
        val cal = Calendar.getInstance().apply {
            timeInMillis = _formState.value.deadlineDate
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _formState.value = _formState.value.copy(
            deadlineTime = timeStr,
            deadlineDate = cal.timeInMillis
        )
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _formState.value = _formState.value.copy(difficulty = difficulty)
    }

    fun updateStatusId(statusId: Long) {
        _formState.value = _formState.value.copy(statusId = statusId)
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        _formState.value = _formState.value.copy(notificationEnabled = enabled)
    }

    fun updateReminderOffset(offset: ReminderOffset) {
        _formState.value = _formState.value.copy(reminderOffset = offset)
    }

    fun updateGroupName(name: String) {
        _formState.value = _formState.value.copy(groupName = name)
    }

    fun addMember(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || _formState.value.members.contains(trimmed)) return
        _formState.value = _formState.value.copy(
            members = _formState.value.members + trimmed
        )
    }

    fun removeMember(index: Int) {
        if (index !in _formState.value.members.indices) return
        val updatedMembers = _formState.value.members.toMutableList().apply { removeAt(index) }
        // Update subtasks references to members
        val updatedSubtasks = _formState.value.subtasks.map { st ->
            when {
                st.assignedMemberIndex == index -> st.copy(assignedMemberIndex = null)
                st.assignedMemberIndex != null && st.assignedMemberIndex > index -> st.copy(assignedMemberIndex = st.assignedMemberIndex - 1)
                else -> st
            }
        }
        _formState.value = _formState.value.copy(
            members = updatedMembers,
            subtasks = updatedSubtasks
        )
    }

    fun addSubtask(title: String, assignedMemberIndex: Int?) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        val newItem = SubtaskFormItem(title = trimmed, assignedMemberIndex = assignedMemberIndex)
        _formState.value = _formState.value.copy(
            subtasks = _formState.value.subtasks + newItem
        )
    }

    fun removeSubtask(index: Int) {
        if (index in _formState.value.subtasks.indices) {
            val updated = _formState.value.subtasks.toMutableList().apply { removeAt(index) }
            _formState.value = _formState.value.copy(subtasks = updated)
        }
    }

    fun addAttachment(attachment: Attachment) {
        _formState.value = _formState.value.copy(
            attachments = _formState.value.attachments + attachment
        )
    }

    fun removeAttachment(index: Int) {
        if (index in _formState.value.attachments.indices) {
            val removed = _formState.value.attachments[index]
            appContext?.let { FileStorageHelper.deleteTempFile(it, removed) }
            val updated = _formState.value.attachments.toMutableList().apply { removeAt(index) }
            _formState.value = _formState.value.copy(attachments = updated)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!_formState.value.isSaving) {
            appContext?.let { ctx ->
                _formState.value.attachments.forEach { att ->
                    FileStorageHelper.deleteTempFile(ctx, att)
                }
            }
        }
    }

    fun saveTask(onSuccess: () -> Unit) {
        val state = _formState.value
        val errors = mutableMapOf<String, String>()

        if (state.title.trim().isEmpty()) {
            errors["title"] = "Judul tugas tidak boleh kosong"
        }
        if (state.subject.trim().isEmpty()) {
            errors["subject"] = "Pilih atau masukkan mata pelajaran"
        }

        if (errors.isNotEmpty()) {
            _formState.value = state.copy(errors = errors)
            return
        }

        _formState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val timeParts = state.deadlineTime.split(":")
                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 23
                val min = timeParts.getOrNull(1)?.toIntOrNull() ?: 59
                val finalDeadlineMillis = Calendar.getInstance().apply {
                    timeInMillis = state.deadlineDate
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, min)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val memberEntities = state.members.map { Member(name = it, taskId = taskId ?: 0) }

                val task = Task(
                    id = taskId ?: 0,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    subject = state.subject.trim(),
                    type = state.type,
                    difficulty = state.difficulty,
                    statusId = state.statusId,
                    deadlineDate = finalDeadlineMillis,
                    deadlineTime = state.deadlineTime,
                    groupName = if (state.type == TaskType.KELOMPOK) state.groupName.trim() else "",
                    notificationEnabled = state.notificationEnabled,
                    reminderOffset = state.reminderOffset,
                    tag = state.tag,
                    createdAt = if (state.isEditMode) state.createdAt else System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // Commit temporary attachments to permanent storage upon saving (AGENTS.md Rule B)
                val permanentAttachments = appContext?.let { ctx ->
                    state.attachments.map { att ->
                        FileStorageHelper.commitAttachment(ctx, att)
                    }
                } ?: state.attachments

                if (state.isEditMode && taskId != null) {
                    val subtaskEntities = state.subtasks.map { st ->
                        Subtask(
                            id = st.id,
                            taskId = taskId,
                            title = st.title,
                            assignedMemberId = null,
                            isCompleted = st.isCompleted,
                            assignedMemberIndex = st.assignedMemberIndex
                        )
                    }
                    taskRepository.updateTaskWithDetails(task, memberEntities, subtaskEntities, permanentAttachments)

                    // Update notifications
                    appContext?.let {
                        val status = state.availableStatuses.find { s -> s.id == task.statusId }
                        val updatedTaskWithStatus = task.copy(statusName = status?.name ?: "")
                        NotificationScheduler.updateTaskNotifications(it, updatedTaskWithStatus)
                    }
                } else {
                    val subtaskEntities = state.subtasks.map { st ->
                        Subtask(
                            id = 0,
                            taskId = 0,
                            title = st.title,
                            assignedMemberId = null,
                            isCompleted = false,
                            assignedMemberIndex = st.assignedMemberIndex
                        )
                    }
                    val newTaskId = taskRepository.insertTaskWithDetails(task, memberEntities, subtaskEntities, permanentAttachments)

                    // Schedule notification
                    appContext?.let {
                        val status = state.availableStatuses.find { s -> s.id == task.statusId }
                        val taskWithId = task.copy(id = newTaskId, statusName = status?.name ?: "")
                        NotificationScheduler.updateTaskNotifications(it, taskWithId)
                    }
                }

                _formState.value = _formState.value.copy(isSaving = false)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _formState.value = _formState.value.copy(
                    isSaving = false,
                    errors = mapOf("general" to "Gagal menyimpan tugas: ${e.message}")
                )
            }
        }
    }
}

class TaskFormViewModelFactory(
    private val taskId: Long?,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val appContext: Context? = null,
    private val initialSubject: String? = null,
    private val initialDeadlineDate: Long? = null,
    private val initialTag: TaskTag? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskFormViewModel(taskId, taskRepository, subjectRepository, statusRepository, appContext, initialSubject, initialDeadlineDate, initialTag) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
