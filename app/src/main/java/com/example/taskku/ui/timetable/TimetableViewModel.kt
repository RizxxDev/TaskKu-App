package com.example.taskku.ui.timetable

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.TimetableRepository
import com.example.taskku.domain.model.SchoolDay
import com.example.taskku.domain.model.Subject
import com.example.taskku.domain.model.TimetableItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

fun calculateNextMeetingDate(item: TimetableItem): Long {
    val cal = Calendar.getInstance()
    val today = SchoolDay.currentDayOfWeek()
    val daysDiff = (item.dayOfWeek - today + 7) % 7
    // If today is the class, next meeting is in 7 days.
    // If class is later this week, next week's meeting is that day + 7 days.
    val daysToAdd = if (daysDiff == 0) 7 else daysDiff + 7
    cal.add(Calendar.DAY_OF_YEAR, daysToAdd)

    val parts = item.startTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 7
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, min)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

class TimetableViewModel(
    private val timetableRepository: TimetableRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    @Immutable
    data class UiState(
        val selectedDay: Int = 1, // 1 = Senin .. 6 = Sabtu (or 7 = Minggu)
        val timetablesByDay: Map<Int, List<TimetableItem>> = emptyMap(),
        val currentDayTimetables: List<TimetableItem> = emptyList(),
        val availableSubjects: List<Subject> = emptyList(),
        val isLoading: Boolean = true
    )

    private val initialDay = run {
        val current = SchoolDay.currentDayOfWeek()
        if (current in 1..6) current else 1
    }

    private val _selectedDay = MutableStateFlow(initialDay)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    val uiState: StateFlow<UiState> = combine(
        timetableRepository.getAllTimetables(),
        subjectRepository.getVisibleSubjects(),
        _selectedDay
    ) { timetables, subjects, day ->
        val grouped = timetables.groupBy { it.dayOfWeek }
            .mapValues { (_, list) -> list.sortedBy { it.startTime } }
        val currentList = grouped[day] ?: emptyList()

        UiState(
            selectedDay = day,
            timetablesByDay = grouped,
            currentDayTimetables = currentList,
            availableSubjects = subjects,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(selectedDay = initialDay, isLoading = true)
    )

    fun onSelectDay(day: Int) {
        _selectedDay.value = day
    }

    fun saveTimetable(
        id: Long = 0,
        subject: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        room: String,
        teacher: String
    ) {
        viewModelScope.launch {
            val item = TimetableItem(
                id = id,
                subject = subject.trim(),
                dayOfWeek = dayOfWeek,
                startTime = startTime.trim(),
                endTime = endTime.trim(),
                room = room.trim(),
                teacher = teacher.trim()
            )
            if (id > 0) {
                timetableRepository.updateTimetable(item)
            } else {
                timetableRepository.insertTimetable(item)
            }
        }
    }

    fun deleteTimetable(id: Long) {
        viewModelScope.launch {
            timetableRepository.deleteTimetableById(id)
        }
    }
}

class TimetableViewModelFactory(
    private val timetableRepository: TimetableRepository,
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimetableViewModel(timetableRepository, subjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
