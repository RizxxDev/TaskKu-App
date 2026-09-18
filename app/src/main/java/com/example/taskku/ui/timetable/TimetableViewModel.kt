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
import java.time.ZonedDateTime

fun calculateNextMeetingDate(
    item: TimetableItem,
    referenceZonedDateTime: ZonedDateTime = ZonedDateTime.now()
): Long {
    val today = referenceZonedDateTime.dayOfWeek.value // 1 = Senin .. 7 = Minggu
    val daysDiff = (item.dayOfWeek - today + 7) % 7
    // If today is the class day, next week's meeting is in 7 days.
    // If the class is later this week (item.dayOfWeek > today), the upcoming meeting this week is in daysDiff days,
    // so the next meeting after that (pertemuan minggu depan) is daysDiff + 7 days.
    // If the class was earlier this week (item.dayOfWeek < today), this week's class has already passed,
    // so the upcoming occurrence (in daysDiff days) is already next week's meeting.
    val daysToAdd = when {
        daysDiff == 0 -> 7L
        item.dayOfWeek > today -> (daysDiff + 7).toLong()
        else -> daysDiff.toLong()
    }

    val parts = item.startTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 7
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val targetDate = referenceZonedDateTime.plusDays(daysToAdd)
        .withHour(hour)
        .withMinute(min)
        .withSecond(0)
        .withNano(0)
    return targetDate.toInstant().toEpochMilli()
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
