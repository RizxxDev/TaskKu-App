package com.example.taskku.ui.dashboard

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

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var viewModel: DashboardViewModel

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
    fun dashboard_calculatesUrgentAndStatsCorrectly() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // 1. Urgent task (due in 2 days, not done)
        val urgentTask = Task(
            id = 1,
            title = "Tugas Mendesak",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1,
            statusName = "Belum Dikerjakan",
            deadlineDate = now + (2 * oneDayMillis)
        )

        // 2. Non-urgent task (due in 5 days, not done)
        val upcomingTask = Task(
            id = 2,
            title = "Tugas Nanti",
            subject = "Fisika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 2,
            statusName = "Sedang Dikerjakan",
            deadlineDate = now + (5 * oneDayMillis)
        )

        // 3. Overdue task (past deadline, not done)
        val overdueTask = Task(
            id = 3,
            title = "Tugas Terlambat",
            subject = "Kimia",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SULIT,
            statusId = 1,
            statusName = "Belum Dikerjakan",
            deadlineDate = now - (2 * oneDayMillis)
        )

        // 4. Completed task (due today, but done)
        val completedTask = Task(
            id = 4,
            title = "Tugas Selesai",
            subject = "Biologi",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 3,
            statusName = "Selesai",
            deadlineDate = now + (1 * oneDayMillis)
        )

        taskRepository.tasksFlow.value = listOf(urgentTask, upcomingTask, overdueTask, completedTask)
        viewModel = DashboardViewModel(taskRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        // Urgent list should only contain urgentTask (not completed, not overdue, within 3 days)
        assertEquals(1, state.urgentTasks.size)
        assertEquals(1L, state.urgentTasks[0].id)

        // Stats check
        assertEquals(2, state.pendingCount) // task 1 and task 3
        assertEquals(1, state.inProgressCount) // task 2
        assertEquals(1, state.doneCount) // task 4
        assertEquals(1, state.overdueCount) // task 3
    }

    @Test
    fun dashboard_handlesEmptyTasks() = runTest(testDispatcher) {
        taskRepository.tasksFlow.value = emptyList()
        viewModel = DashboardViewModel(taskRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertEquals(0, state.urgentTasks.size)
        assertEquals(0, state.pendingCount)
        assertEquals(0, state.inProgressCount)
        assertEquals(0, state.doneCount)
        assertEquals(0, state.overdueCount)
        assertEquals(0f, state.thisWeekProgress, 0.001f)
    }
}
