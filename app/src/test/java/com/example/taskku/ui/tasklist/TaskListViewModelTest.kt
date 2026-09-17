package com.example.taskku.ui.tasklist

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.SortDirection
import com.example.taskku.domain.model.SortOption
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import com.example.taskku.fakes.FakeStatusRepository
import com.example.taskku.fakes.FakeSubjectRepository
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var subjectRepository: FakeSubjectRepository
    private lateinit var statusRepository: FakeStatusRepository
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = FakeTaskRepository()
        subjectRepository = FakeSubjectRepository()
        statusRepository = FakeStatusRepository()

        // Populate sample tasks
        val t1 = Task(
            id = 1,
            title = "PR Matematika Bab 3",
            description = "Halaman 45-50",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            statusName = "Belum Dikerjakan",
            deadlineDate = 100000L,
            createdAt = 1000L
        )
        val t2 = Task(
            id = 2,
            title = "Tugas Kelompok Fisika",
            description = "Praktikum Hukum Ohm",
            subject = "Fisika",
            type = TaskType.KELOMPOK,
            difficulty = Difficulty.SULIT,
            statusId = 2,
            statusName = "Sedang Dikerjakan",
            groupName = "Kelompok Einstein",
            deadlineDate = 50000L,
            createdAt = 2000L
        )
        val t3 = Task(
            id = 3,
            title = "Makalah Bahasa Indonesia",
            description = "Teks Eksposisi",
            subject = "Bahasa Indonesia",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 3,
            statusName = "Selesai",
            deadlineDate = 200000L,
            createdAt = 3000L
        )
        taskRepository.tasksFlow.value = listOf(t1, t2, t3)

        viewModel = TaskListViewModel(
            taskRepository = taskRepository,
            subjectRepository = subjectRepository,
            statusRepository = statusRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadTasks_displaysAllTasksInitially() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.tasks.size)
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun filterBySearchQuery_filtersCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Matematika")
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        assertEquals(1, state.tasks.size)
        assertEquals("PR Matematika Bab 3", state.tasks[0].title)

        // Search by group name
        viewModel.onSearchQueryChange("Einstein")
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.tasks.size)
        assertEquals(2L, state.tasks[0].id)

        // Clear query
        viewModel.onSearchQueryChange("")
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(3, state.tasks.size)
    }

    @Test
    fun filterBySubject_filtersCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onSubjectToggle("Matematika")
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        assertEquals(1, state.tasks.size)
        assertEquals("Matematika", state.tasks[0].subject)

        // Toggle another subject (multi-select)
        viewModel.onSubjectToggle("Fisika")
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(2, state.tasks.size)

        // Untoggle Matematika
        viewModel.onSubjectToggle("Matematika")
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.tasks.size)
        assertEquals("Fisika", state.tasks[0].subject)
    }

    @Test
    fun subjectFilterBar_onlyShowsSubjectsWithActiveTasks() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val subjectNames = state.subjects.map { it.name }
        assertTrue(subjectNames.contains("Matematika"))
        assertTrue(subjectNames.contains("Fisika"))
        assertFalse(subjectNames.contains("Bahasa Inggris"))
    }

    @Test
    fun sortOption_sortsByDeadlineCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onSortChange(SortOption.DEADLINE)
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        assertEquals(SortDirection.ASC, state.sortDirection)
        // t2 has 50000, t1 has 100000, t3 has 200000
        assertEquals(2L, state.tasks[0].id)
        assertEquals(1L, state.tasks[1].id)
        assertEquals(3L, state.tasks[2].id)

        // Toggle direction to DESC
        viewModel.onDirectionToggle()
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(SortDirection.DESC, state.sortDirection)
        assertEquals(3L, state.tasks[0].id)
        assertEquals(1L, state.tasks[1].id)
        assertEquals(2L, state.tasks[2].id)
    }

    @Test
    fun sortOption_sortsByDifficultyCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onSortChange(SortOption.DIFFICULTY)
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        // Difficulty default direction is DESC (Sulit -> Sedang -> Mudah)
        assertEquals(SortDirection.DESC, state.sortDirection)
        assertEquals(Difficulty.SULIT, state.tasks[0].difficulty)
        assertEquals(Difficulty.SEDANG, state.tasks[1].difficulty)
        assertEquals(Difficulty.MUDAH, state.tasks[2].difficulty)

        // Toggle to ASC (Mudah -> Sedang -> Sulit)
        viewModel.onDirectionToggle()
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(SortDirection.ASC, state.sortDirection)
        assertEquals(Difficulty.MUDAH, state.tasks[0].difficulty)
        assertEquals(Difficulty.SEDANG, state.tasks[1].difficulty)
        assertEquals(Difficulty.SULIT, state.tasks[2].difficulty)
    }

    @Test
    fun selectionMode_andBulkDelete_worksCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onToggleTaskSelection(1L)
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        assertTrue(state.isSelectionMode)
        assertEquals(setOf(1L), state.selectedTaskIds)

        viewModel.onToggleTaskSelection(2L)
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(setOf(1L, 2L), state.selectedTaskIds)

        // Bulk delete
        viewModel.onBulkDelete()
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertFalse(state.isSelectionMode)
        assertEquals(1, state.tasks.size)
        assertEquals(3L, state.tasks[0].id)
    }

    @Test
    fun deleteTask_removesSingleTask() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onDeleteTask(3L)
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(2, state.tasks.size)
        assertTrue(state.tasks.none { it.id == 3L })
    }
}
