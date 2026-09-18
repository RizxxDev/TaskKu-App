package com.example.taskku.ui.taskform

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.Member
import com.example.taskku.domain.model.Subtask
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import com.example.taskku.fakes.FakeStatusRepository
import com.example.taskku.fakes.FakeSubjectRepository
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var subjectRepository: FakeSubjectRepository
    private lateinit var statusRepository: FakeStatusRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = FakeTaskRepository()
        subjectRepository = FakeSubjectRepository()
        statusRepository = FakeStatusRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun validation_failsWhenTitleOrSubjectEmpty() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        viewModel.updateSubject("")
        var saved = false
        viewModel.saveTask { saved = true }
        testScheduler.advanceUntilIdle()

        assertFalse(saved)
        val errors = viewModel.formState.value.errors
        assertTrue(errors.containsKey("title"))
        assertTrue(errors.containsKey("subject"))

        // Provide title, error for title should be cleared
        viewModel.updateTitle("Tugas Biologi")
        assertFalse(viewModel.formState.value.errors.containsKey("title"))

        // Provide subject
        viewModel.updateSubject("Biologi")
        assertFalse(viewModel.formState.value.errors.containsKey("subject"))
    }

    @Test
    fun groupTask_memberAndSubtaskManagement() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        viewModel.updateType(TaskType.KELOMPOK)
        viewModel.updateGroupName("Kelompok 1")

        viewModel.addMember("Andi")
        viewModel.addMember("Budi")
        viewModel.addMember("Citra")
        // Try adding duplicate
        viewModel.addMember("Andi")

        val members = viewModel.formState.value.members
        assertEquals(3, members.size)
        assertEquals(listOf("Andi", "Budi", "Citra"), members)

        // Add subtask assigned to Budi (index 1)
        viewModel.addSubtask("Analisis Data", assignedMemberIndex = 1)
        assertEquals(1, viewModel.formState.value.subtasks.size)
        assertEquals(1, viewModel.formState.value.subtasks[0].assignedMemberIndex)

        // Remove Andi (index 0) -> Budi becomes index 0, subtask assignedMemberIndex should decrement to 0
        viewModel.removeMember(0)
        assertEquals(2, viewModel.formState.value.members.size)
        assertEquals("Budi", viewModel.formState.value.members[0])
        assertEquals(0, viewModel.formState.value.subtasks[0].assignedMemberIndex)
    }

    @Test
    fun saveNewTask_insertsIntoRepository() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        viewModel.updateTitle("Proyek Fisika")
        viewModel.updateSubject("Fisika")
        viewModel.updateDifficulty(Difficulty.SULIT)
        viewModel.updateType(TaskType.KELOMPOK)
        viewModel.updateGroupName("Tim Newton")
        viewModel.addMember("Doni")
        viewModel.addSubtask("Rancang Rangkaian", assignedMemberIndex = 0)

        var saved = false
        viewModel.saveTask { saved = true }
        testScheduler.advanceUntilIdle()

        assertTrue(saved)
        val allTasks = taskRepository.getAllTasks().first()
        assertEquals(1, allTasks.size)
        val savedTask = allTasks[0]
        assertEquals("Proyek Fisika", savedTask.title)
        assertEquals("Fisika", savedTask.subject)
        assertEquals(Difficulty.SULIT, savedTask.difficulty)
        assertEquals(TaskType.KELOMPOK, savedTask.type)
        assertEquals("Tim Newton", savedTask.groupName)
        assertEquals(1, savedTask.members.size)
        assertEquals("Doni", savedTask.members[0].name)
        assertEquals(1, savedTask.subtasks.size)
        assertEquals("Rancang Rangkaian", savedTask.subtasks[0].title)
        assertEquals(savedTask.members[0].id, savedTask.subtasks[0].assignedMemberId)
    }

    @Test
    fun updateDeadlineTime_synchronizesDeadlineDateMillis() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        viewModel.updateDeadlineTime("10:30")
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = viewModel.formState.value.deadlineDate
        }
        assertEquals(10, cal.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(30, cal.get(java.util.Calendar.MINUTE))
    }

    @Test
    fun editExistingTask_loadsAndUpdates() = runTest(testDispatcher) {
        val existingTask = Task(
            id = 42,
            title = "Tugas Lama",
            subject = "Kimia",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            deadlineDate = 1700000000000L,
            members = emptyList(),
            subtasks = emptyList()
        )
        taskRepository.tasksFlow.value = listOf(existingTask)

        val viewModel = TaskFormViewModel(42L, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        val state = viewModel.formState.value
        assertTrue(state.isEditMode)
        assertEquals("Tugas Lama", state.title)
        assertEquals("Kimia", state.subject)

        viewModel.updateTitle("Tugas Diperbarui")
        var saved = false
        viewModel.saveTask { saved = true }
        testScheduler.advanceUntilIdle()

        assertTrue(saved)
        val updatedTasks = taskRepository.getAllTasks().first()
        val updatedTask = updatedTasks.find { it.id == 42L }
        assertEquals("Tugas Diperbarui", updatedTask?.title)
        assertEquals(existingTask.createdAt, updatedTask?.createdAt)
    }

    @Test
    fun updateReminderOffset_updatesStateAndPersists() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        assertEquals(com.example.taskku.domain.model.ReminderOffset.ONE_HOUR_BEFORE, viewModel.formState.value.reminderOffset)

        viewModel.updateTitle("Tugas Baru")
        viewModel.updateSubject("Matematika")
        viewModel.updateReminderOffset(com.example.taskku.domain.model.ReminderOffset.ONE_DAY_19_00)

        assertEquals(com.example.taskku.domain.model.ReminderOffset.ONE_DAY_19_00, viewModel.formState.value.reminderOffset)

        var saved = false
        viewModel.saveTask { saved = true }
        testScheduler.advanceUntilIdle()

        assertTrue(saved)
        val tasks = taskRepository.getAllTasks().first()
        val created = tasks.find { it.title == "Tugas Baru" }
        assertNotNull(created)
        assertEquals(com.example.taskku.domain.model.ReminderOffset.ONE_DAY_19_00, created?.reminderOffset)
    }

    @Test
    fun updateTag_updatesStateAndPersists() = runTest(testDispatcher) {
        val viewModel = TaskFormViewModel(null, taskRepository, subjectRepository, statusRepository)
        testScheduler.advanceUntilIdle()

        assertEquals(com.example.taskku.domain.model.TaskTag.PR, viewModel.formState.value.tag)

        viewModel.updateTitle("Kuis Fisika Bab 4")
        viewModel.updateSubject("Fisika")
        viewModel.updateTag(com.example.taskku.domain.model.TaskTag.KUIS)

        assertEquals(com.example.taskku.domain.model.TaskTag.KUIS, viewModel.formState.value.tag)

        var saved = false
        viewModel.saveTask { saved = true }
        testScheduler.advanceUntilIdle()

        assertTrue(saved)
        val created = taskRepository.getAllTasks().first().find { it.title == "Kuis Fisika Bab 4" }
        assertNotNull(created)
        assertEquals(com.example.taskku.domain.model.TaskTag.KUIS, created?.tag)
    }

    @Test
    fun initialValues_prefilled_whenProvidedFromTimetableShortcut() = runTest(testDispatcher) {
        val targetDeadline = 1800000000000L
        val viewModel = TaskFormViewModel(
            taskId = null,
            taskRepository = taskRepository,
            subjectRepository = subjectRepository,
            statusRepository = statusRepository,
            appContext = null,
            initialSubject = "Biologi",
            initialDeadlineDate = targetDeadline,
            initialTag = com.example.taskku.domain.model.TaskTag.PR
        )
        testScheduler.advanceUntilIdle()

        val state = viewModel.formState.value
        assertEquals("Biologi", state.subject)
        assertEquals(targetDeadline, state.deadlineDate)
        assertEquals(com.example.taskku.domain.model.TaskTag.PR, state.tag)
    }
}
