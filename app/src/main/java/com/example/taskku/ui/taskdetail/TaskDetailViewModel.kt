package com.example.taskku.ui.taskdetail

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.Status
import com.example.taskku.domain.model.Task
import com.example.taskku.notification.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
    private val statusRepository: StatusRepository,
    private val appContext: Context? = null
) : ViewModel() {

    @Immutable
    data class UiState(
        val task: Task? = null,
        val statuses: List<Status> = emptyList(),
        val isLoading: Boolean = false
    )

    val uiState: StateFlow<UiState> = combine(
        taskRepository.getTaskById(taskId),
        statusRepository.getAllStatuses()
    ) { task, statuses ->
        UiState(
            task = task,
            statuses = statuses,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = true)
    )

    fun onToggleSubtask(subtaskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleSubtaskCompletion(subtaskId, isCompleted)
        }
    }

    fun onChangeStatus(statusId: Long) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(taskId, statusId)

            val currentTask = uiState.value.task
            val targetStatus = uiState.value.statuses.find { it.id == statusId }
            if (currentTask != null && targetStatus != null) {
                val updatedTask = currentTask.copy(
                    statusId = statusId,
                    statusName = targetStatus.name,
                    statusColorHex = targetStatus.colorHex
                )
                appContext?.let {
                    NotificationScheduler.updateTaskNotifications(it, updatedTask)
                }
            }
        }
    }

    fun onDeleteTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            appContext?.let {
                NotificationScheduler.cancelNotifications(it, taskId)
            }
            taskRepository.deleteTaskById(taskId)
            onSuccess()
        }
    }
}

class TaskDetailViewModelFactory(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
    private val statusRepository: StatusRepository,
    private val appContext: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailViewModel(taskId, taskRepository, statusRepository, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
