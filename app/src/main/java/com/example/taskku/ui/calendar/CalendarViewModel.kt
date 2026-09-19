package com.example.taskku.ui.calendar

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.Task
import com.example.taskku.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CalendarViewModel(
    private val taskRepository: TaskRepository,
    private val defaultDispatcher: CoroutineDispatcher = DispatcherProvider.defaultComputation
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _selectedLocalDate = MutableStateFlow(LocalDate.now())
    val selectedLocalDate: StateFlow<LocalDate> = _selectedLocalDate.asStateFlow()

    // Backward-compatible synchronous Calendar StateFlow bridges for tests/consumers
    private val _currentMonth = MutableStateFlow(Calendar.getInstance().apply {
        val ym = YearMonth.now()
        set(Calendar.YEAR, ym.year)
        set(Calendar.MONTH, ym.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    })
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    @Immutable
    data class UiState(
        val currentYearMonth: YearMonth = YearMonth.now(),
        val selectedDate: LocalDate = LocalDate.now(),
        val currentMonthMillis: Long = 0L,
        val selectedDateMillis: Long = 0L,
        val tasksByDate: Map<String, List<Task>> = emptyMap(),
        val selectedDateTasks: List<Task> = emptyList(),
        val isLoading: Boolean = true
    ) {
        val currentMonth: Calendar
            get() = Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
    }

    private val tasksByDateFlow: Flow<Map<String, List<Task>>> = taskRepository.getAllTasks()
        .map { tasks ->
            tasks.groupBy { task -> formatDateKey(task.deadlineDate) }
        }
        .distinctUntilChanged()
        .flowOn(defaultDispatcher)

    val uiState: StateFlow<UiState> = combine(
        tasksByDateFlow,
        _currentYearMonth,
        _selectedLocalDate
    ) { tasksByDate, yearMonth, selectedLocalDate ->
        val selectedKey = formatDateKey(selectedLocalDate)
        val selectedTasks = tasksByDate[selectedKey] ?: emptyList()

        val startOfMonthMillis = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val selectedMillis = selectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        UiState(
            currentYearMonth = yearMonth,
            selectedDate = selectedLocalDate,
            currentMonthMillis = startOfMonthMillis,
            selectedDateMillis = selectedMillis,
            tasksByDate = tasksByDate,
            selectedDateTasks = selectedTasks,
            isLoading = false
        )
    }.flowOn(defaultDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = run {
            val ym = YearMonth.now()
            val ld = LocalDate.now()
            UiState(
                currentYearMonth = ym,
                selectedDate = ld,
                currentMonthMillis = ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                selectedDateMillis = ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isLoading = true
            )
        }
    )

    companion object {
        fun formatDateKey(millis: Long): String {
            val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        fun formatDateKey(date: LocalDate): String {
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        fun formatDateKey(cal: Calendar): String {
            return String.format(
                java.util.Locale.ROOT,
                "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    fun onNextMonth() {
        val next = _currentYearMonth.value.plusMonths(1)
        _currentYearMonth.value = next
        _currentMonth.value = Calendar.getInstance().apply {
            set(Calendar.YEAR, next.year)
            set(Calendar.MONTH, next.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun onPreviousMonth() {
        val prev = _currentYearMonth.value.minusMonths(1)
        _currentYearMonth.value = prev
        _currentMonth.value = Calendar.getInstance().apply {
            set(Calendar.YEAR, prev.year)
            set(Calendar.MONTH, prev.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedLocalDate.value = date
        _selectedDate.value = Calendar.getInstance().apply {
            set(Calendar.YEAR, date.year)
            set(Calendar.MONTH, date.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
        }
    }

    fun onDateSelected(millis: Long) {
        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        _selectedLocalDate.value = localDate
        _selectedDate.value = Calendar.getInstance().apply {
            timeInMillis = millis
        }
    }

    fun onDateSelected(calendar: Calendar) {
        _selectedDate.value = calendar.clone() as Calendar
        val localDate = Instant.ofEpochMilli(calendar.timeInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        _selectedLocalDate.value = localDate
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
