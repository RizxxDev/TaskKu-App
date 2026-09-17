package com.example.taskku.ui.calendar

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.Task
import kotlinx.coroutines.flow.*
import java.util.*

class CalendarViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    })
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    @Immutable
    data class UiState(
        val currentMonthMillis: Long,
        val selectedDateMillis: Long,
        val tasksByDate: Map<String, List<Task>> = emptyMap(),
        val selectedDateTasks: List<Task> = emptyList(),
        val isLoading: Boolean = true
    ) {
        val currentMonth: Calendar
            get() = Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
        val selectedDate: Calendar
            get() = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    }

    private val tasksByDateFlow: Flow<Map<String, List<Task>>> = taskRepository.getAllTasks()
        .map { tasks ->
            tasks.groupBy { task -> formatDateKey(task.deadlineDate) }
        }
        .distinctUntilChanged()

    val uiState: StateFlow<UiState> = combine(
        tasksByDateFlow,
        _currentMonth,
        _selectedDate
    ) { tasksByDate, month, selected ->
        val selectedKey = formatDateKey(selected)
        val selectedTasks = tasksByDate[selectedKey] ?: emptyList()

        UiState(
            currentMonthMillis = month.timeInMillis,
            selectedDateMillis = selected.timeInMillis,
            tasksByDate = tasksByDate,
            selectedDateTasks = selectedTasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(
            currentMonthMillis = _currentMonth.value.timeInMillis,
            selectedDateMillis = _selectedDate.value.timeInMillis,
            isLoading = true
        )
    )

    companion object {
        private fun formatDateKey(millis: Long): String {
            val cal = Calendar.getInstance().apply { timeInMillis = millis }
            return String.format(
                Locale.ROOT,
                "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        private fun formatDateKey(cal: Calendar): String {
            return String.format(
                Locale.ROOT,
                "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    fun onNextMonth() {
        val next = (_currentMonth.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _currentMonth.value = next
    }

    fun onPreviousMonth() {
        val prev = (_currentMonth.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _currentMonth.value = prev
    }

    fun onDateSelected(calendar: Calendar) {
        _selectedDate.value = calendar
    }

    fun onDateSelected(millis: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        _selectedDate.value = cal
    }
}

class CalendarViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
