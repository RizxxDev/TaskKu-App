package com.example.taskku.ui.dashboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.data.repository.TimetableRepository
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TimetableItem
import com.example.taskku.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class DashboardViewModel(
    private val taskRepository: TaskRepository,
    private val timetableRepository: TimetableRepository? = null,
    private val defaultDispatcher: CoroutineDispatcher = DispatcherProvider.defaultComputation
) : ViewModel() {

    @Immutable
    data class UiState(
        val urgentTasks: List<Task> = emptyList(),
        val pendingCount: Int = 0,
        val inProgressCount: Int = 0,
        val doneCount: Int = 0,
        val overdueCount: Int = 0,
        val thisWeekTotalCount: Int = 0,
        val thisWeekDoneCount: Int = 0,
        val thisWeekProgress: Float = 0f,
        val nextClassToday: TimetableItem? = null,
        val isLoading: Boolean = false
    )

    private val nextClassFlow = timetableRepository?.getUpcomingNextClass() ?: flowOf(null)

    val uiState: StateFlow<UiState> = combine(
        taskRepository.getTaskSummaries(),
        nextClassFlow
    ) { tasks, nextClass ->
        val now = System.currentTimeMillis()

        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfWeek = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 7)
        val endOfWeek = cal.timeInMillis

        var pending = 0
        var inProgress = 0
        var done = 0
        var overdue = 0
        val urgent = mutableListOf<Task>()
        var weekTotal = 0
        var weekDone = 0

        tasks.forEach { task ->
            val isDone = task.isCompleted
            val isInProg = task.statusName.equals("Sedang Dikerjakan", ignoreCase = true)

            if (isDone) {
                done++
            } else if (isInProg) {
                inProgress++
            } else {
                pending++
            }

            if (!isDone && task.deadlineDate < now) {
                overdue++
            }

            if (!isDone && task.deadlineDate >= now) {
                val diffDays = (task.deadlineDate - now) / (1000 * 60 * 60 * 24)
                if (diffDays <= 3) {
                    urgent.add(task)
                }
            }

            if (task.deadlineDate in startOfWeek until endOfWeek) {
                weekTotal++
                if (isDone) weekDone++
            }
        }

        urgent.sortBy { it.deadlineDate }

        val progress = if (weekTotal > 0) weekDone.toFloat() / weekTotal.toFloat() else 0f

        UiState(
            urgentTasks = urgent,
            pendingCount = pending,
            inProgressCount = inProgress,
            doneCount = done,
            overdueCount = overdue,
            thisWeekTotalCount = weekTotal,
            thisWeekDoneCount = weekDone,
            thisWeekProgress = progress,
            nextClassToday = nextClass,
            isLoading = false
        )
    }.flowOn(defaultDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = false)
    )
}

class DashboardViewModelFactory(
    private val taskRepository: TaskRepository,
    private val timetableRepository: TimetableRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(taskRepository, timetableRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
