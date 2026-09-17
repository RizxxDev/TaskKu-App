package com.example.taskku.ui.settings

import com.example.taskku.data.preferences.AppPreferences
import com.example.taskku.data.preferences.ThemeMode
import com.example.taskku.export.ExportImportManager
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var appPreferences: AppPreferences
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var subjectRepository: FakeSubjectRepository
    private lateinit var statusRepository: FakeStatusRepository
    private lateinit var exportImportManager: ExportImportManager
    private lateinit var viewModel: SettingsViewModel
    private lateinit var tempDir: File

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tempDir = File(System.getProperty("java.io.tmpdir"), "taskku_test_${System.currentTimeMillis()}").apply { mkdirs() }
        appPreferences = AppPreferences()
        taskRepository = FakeTaskRepository()
        subjectRepository = FakeSubjectRepository()
        statusRepository = FakeStatusRepository()
        exportImportManager = ExportImportManager(tempDir, taskRepository, subjectRepository, statusRepository)

        viewModel = SettingsViewModel(
            appPreferences = appPreferences,
            subjectRepository = subjectRepository,
            statusRepository = statusRepository,
            exportImportManager = exportImportManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        tempDir.deleteRecursively()
    }

    @Test
    fun theme_changesCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        assertEquals(ThemeMode.SYSTEM, viewModel.uiState.value.themeMode)

        viewModel.onThemeChange(ThemeMode.DARK)
        testScheduler.advanceUntilIdle()
        assertEquals(ThemeMode.DARK, viewModel.uiState.value.themeMode)

        viewModel.onThemeChange(ThemeMode.LIGHT)
        testScheduler.advanceUntilIdle()
        assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.themeMode)
    }

    @Test
    fun subject_crudAndVisibility_worksCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val initialCount = viewModel.uiState.value.subjects.size

        // Add subject
        viewModel.onAddSubject("Sosiologi")
        testScheduler.advanceUntilIdle()
        val updatedState = viewModel.uiState.value
        assertEquals(initialCount + 1, updatedState.subjects.size)
        assertTrue(updatedState.subjects.any { it.name == "Sosiologi" })

        // Toggle visibility
        val sosiologi = updatedState.subjects.find { it.name == "Sosiologi" }!!
        viewModel.onToggleSubjectVisibility(sosiologi.id, false)
        testScheduler.advanceUntilIdle()
        val toggledState = viewModel.uiState.value
        assertFalse(toggledState.subjects.find { it.name == "Sosiologi" }!!.isVisible)

        // Delete subject
        viewModel.onDeleteSubject(sosiologi.id)
        testScheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertEquals(initialCount, finalState.subjects.size)
        assertFalse(finalState.subjects.any { it.name == "Sosiologi" })
    }

    @Test
    fun status_crud_worksCorrectly() = runTest(testDispatcher) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        val initialCount = viewModel.uiState.value.statuses.size

        // Add custom status
        viewModel.onAddStatus("Ditunda", "#FD79A8")
        testScheduler.advanceUntilIdle()
        val updatedState = viewModel.uiState.value
        assertEquals(initialCount + 1, updatedState.statuses.size)
        val ditunda = updatedState.statuses.find { it.name == "Ditunda" }!!
        assertEquals("#FD79A8", ditunda.colorHex)

        // Edit status name and color
        viewModel.onUpdateStatus(ditunda.id, "Ditunda Revisi", "#A29BFE")
        testScheduler.advanceUntilIdle()
        var state = viewModel.uiState.value
        val edited = state.statuses.find { it.id == ditunda.id }!!
        assertEquals("Ditunda Revisi", edited.name)
        assertEquals("#A29BFE", edited.colorHex)

        // Move status up
        val initialIdx = state.statuses.indexOfFirst { it.id == ditunda.id }
        viewModel.onMoveStatusUp(edited)
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        val upIdx = state.statuses.indexOfFirst { it.id == ditunda.id }
        assertEquals(initialIdx - 1, upIdx)

        // Move status down
        viewModel.onMoveStatusDown(edited.copy(sortOrder = state.statuses[upIdx].sortOrder))
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        val downIdx = state.statuses.indexOfFirst { it.id == ditunda.id }
        assertEquals(initialIdx, downIdx)

        // Delete status
        viewModel.onDeleteStatus(ditunda.id)
        testScheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertEquals(initialCount, finalState.statuses.size)
        assertFalse(finalState.statuses.any { it.name == "Ditunda Revisi" })
    }
}
