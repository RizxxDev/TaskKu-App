package com.example.taskku.ui.taskdetail

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.Subtask
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import com.example.taskku.fakes.FakeStatusRepository
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var statusRepository: FakeStatusRepository
    private lateinit var viewModel: TaskDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = FakeTaskRepository()
        statusRepository = FakeStatusRepository()

        val sampleTask = Task(
            id = 10,
            title = "Tugas Biologi Sel",
            description = "Struktur membran sel",
            subject = "Biologi",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1,
            statusName = "Belum Dikerjakan",
            deadlineDate = 1700000000000L,
            subtasks = listOf(
                Subtask(id = 101, taskId = 10, title = "Gambar organel", isCompleted = false),
                Subtask(id = 102, taskId = 10, title = "Tulis penjelasan", isCompleted = true)
            )
        )
        taskRepository.tasksFlow.value = listOf(sampleTask)

        viewModel = TaskDetailViewModel(
            taskId = 10L,
            taskRepository = taskRepository,
            statusRepository = statusRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadTaskDetail_displaysTaskAndSubtasks() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertNotNull(state.task)
        assertEquals("Tugas Biologi Sel", state.task?.title)
        assertEquals(2, state.task?.subtasks?.size)
        assertEquals(4, state.statuses.size)
    }

    @Test
    fun toggleSubtask_updatesCompletionStatus() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        // Toggle subtask 101 to true
        viewModel.onToggleSubtask(101L, true)
        testScheduler.advanceUntilIdle()

        val updatedTask = taskRepository.getTaskById(10L).first()
        assertTrue(updatedTask?.subtasks?.find { it.id == 101L }?.isCompleted == true)

        // Toggle subtask 102 to false
        viewModel.onToggleSubtask(102L, false)
        testScheduler.advanceUntilIdle()

        val updatedTask2 = taskRepository.getTaskById(10L).first()
        assertFalse(updatedTask2?.subtasks?.find { it.id == 102L }?.isCompleted == true)
    }

    @Test
    fun changeStatus_updatesTaskStatus() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        // Change status to Selesai (id 3)
        viewModel.onChangeStatus(3L)
        testScheduler.advanceUntilIdle()

        val updatedTask = taskRepository.getTaskById(10L).first()
        assertEquals(3L, updatedTask?.statusId)
    }

    @Test
    fun deleteTask_deletesAndTriggersCallback() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        var callbackCalled = false
        viewModel.onDeleteTask(onSuccess = { callbackCalled = true })
        testScheduler.advanceUntilIdle()

        assertTrue(callbackCalled)
        val allTasks = taskRepository.getAllTasks().first()
        assertTrue(allTasks.isEmpty())
    }
}
