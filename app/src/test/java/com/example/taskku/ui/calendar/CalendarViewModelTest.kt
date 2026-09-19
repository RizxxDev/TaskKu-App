package com.example.taskku.ui.calendar

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var viewModel: CalendarViewModel
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = FakeTaskRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun calendar_groupsTasksByDateAndFiltersSelectedDate() = runTest(testDispatcher) {
        val today = Calendar.getInstance()
        val todayMillis = today.timeInMillis

        val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowMillis = tomorrow.timeInMillis

        val taskToday1 = Task(
            id = 1,
            title = "Tugas Hari Ini 1",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            deadlineDate = todayMillis
        )
        val taskToday2 = Task(
            id = 2,
            title = "Tugas Hari Ini 2",
            subject = "Fisika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 2,
            deadlineDate = todayMillis
        )
        val taskTomorrow = Task(
            id = 3,
            title = "Tugas Besok",
            subject = "Kimia",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SULIT,
            statusId = 1,
            deadlineDate = tomorrowMillis
        )

        taskRepository.tasksFlow.value = listOf(taskToday1, taskToday2, taskTomorrow)
        viewModel = CalendarViewModel(taskRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        val todayKey = sdf.format(Date(todayMillis))
        val tomorrowKey = sdf.format(Date(tomorrowMillis))

        assertEquals(2, state.tasksByDate[todayKey]?.size)
        assertEquals(1, state.tasksByDate[tomorrowKey]?.size)
        // Default selectedDate is today
        assertEquals(2, state.selectedDateTasks.size)

        // Select tomorrow
        viewModel.onDateSelected(tomorrow)
        testScheduler.advanceUntilIdle()
        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.selectedDateTasks.size)
        assertEquals("Tugas Besok", updatedState.selectedDateTasks[0].title)
    }

    @Test
    fun calendar_navigatesMonthCorrectly() = runTest(testDispatcher) {
        viewModel = CalendarViewModel(taskRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        val initialMonth = viewModel.currentMonth.value.get(Calendar.MONTH)
        viewModel.onNextMonth()
        val nextMonth = viewModel.currentMonth.value.get(Calendar.MONTH)
        assertEquals((initialMonth + 1) % 12, nextMonth)

        viewModel.onPreviousMonth()
        val revertedMonth = viewModel.currentMonth.value.get(Calendar.MONTH)
        assertEquals(initialMonth, revertedMonth)
    }

    @Test
    fun calendar_selectsDateDirectlyWithLocalDate() = runTest(testDispatcher) {
        viewModel = CalendarViewModel(taskRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        val targetDate = java.time.LocalDate.of(2026, 5, 20)
        viewModel.onDateSelected(targetDate)
        testScheduler.advanceUntilIdle()

        val updatedState = viewModel.uiState.value
        assertEquals(targetDate, updatedState.selectedDate)
        assertEquals(targetDate, viewModel.selectedLocalDate.value)
        assertEquals(targetDate.dayOfMonth, viewModel.selectedDate.value.get(Calendar.DAY_OF_MONTH))
        assertEquals(targetDate.monthValue - 1, viewModel.selectedDate.value.get(Calendar.MONTH))
        assertEquals(targetDate.year, viewModel.selectedDate.value.get(Calendar.YEAR))
    }
}
