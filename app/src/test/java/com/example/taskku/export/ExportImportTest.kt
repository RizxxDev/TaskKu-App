package com.example.taskku.export

import com.example.taskku.domain.model.*
import com.example.taskku.fakes.FakeStatusRepository
import com.example.taskku.fakes.FakeSubjectRepository
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class ExportImportTest {

    private lateinit var tempDir: File
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var subjectRepository: FakeSubjectRepository
    private lateinit var statusRepository: FakeStatusRepository
    private lateinit var appPreferences: com.example.taskku.data.preferences.AppPreferences
    private lateinit var manager: ExportImportManager

    @Before
    fun setup() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "taskku_export_test_${System.currentTimeMillis()}").apply { mkdirs() }
        taskRepository = FakeTaskRepository()
        subjectRepository = FakeSubjectRepository()
        statusRepository = FakeStatusRepository()
        appPreferences = com.example.taskku.data.preferences.AppPreferences()
        manager = ExportImportManager(tempDir, taskRepository, subjectRepository, statusRepository, appPreferences)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun exportAndImport_restoresAllDataAndAttachments() = runBlocking {
        // Set custom sorting preferences
        appPreferences.setSortOption(SortOption.DIFFICULTY)
        appPreferences.setSortDirection(SortDirection.DESC)

        // Create dummy attachment file
        val dummyFile = File(tempDir, "sample_document.pdf").apply {
            writeText("Test attachment content for TaskKu")
        }

        val task = Task(
            id = 1,
            title = "Tugas Uji Ekspor",
            description = "Deskripsi uji",
            subject = "Matematika",
            type = TaskType.KELOMPOK,
            tag = TaskTag.PRAKTIKUM,
            difficulty = Difficulty.SULIT,
            statusId = 1,
            groupName = "Tim Hebat",
            deadlineDate = 1700000000000L,
            members = listOf(Member(id = 1, taskId = 1, name = "Budi")),
            subtasks = listOf(Subtask(id = 1, taskId = 1, title = "Subtugas 1", assignedMemberId = 1, isCompleted = false)),
            attachments = listOf(
                Attachment(
                    id = 1,
                    taskId = 1,
                    fileName = "sample_document.pdf",
                    filePath = dummyFile.absolutePath,
                    fileType = "application/pdf",
                    fileSize = dummyFile.length()
                )
            )
        )
        taskRepository.tasksFlow.value = listOf(task)

        // 1. Export
        val outputStream = ByteArrayOutputStream()
        val exportResult = manager.exportData(outputStream)
        assertTrue(exportResult.isSuccess)
        assertEquals(1, exportResult.getOrNull())

        val exportedBytes = outputStream.toByteArray()
        assertTrue(exportedBytes.isNotEmpty())

        // 2. Clear repository and preferences to simulate new device or fresh install
        val newTaskRepo = FakeTaskRepository()
        val newSubjectRepo = FakeSubjectRepository()
        val newStatusRepo = FakeStatusRepository()
        val newPrefs = com.example.taskku.data.preferences.AppPreferences()
        assertEquals(SortOption.DEADLINE, newPrefs.sortOption.value) // default initial

        val restoreDir = File(tempDir, "restored").apply { mkdirs() }
        val newManager = ExportImportManager(restoreDir, newTaskRepo, newSubjectRepo, newStatusRepo, newPrefs)

        // 3. Import
        val inputStream = ByteArrayInputStream(exportedBytes)
        val importResult = newManager.importData(inputStream)
        assertTrue(importResult.isSuccess)
        assertEquals(1, importResult.getOrNull())

        // Check restored sorting preferences
        assertEquals(SortOption.DIFFICULTY, newPrefs.sortOption.value)
        assertEquals(SortDirection.DESC, newPrefs.sortDirection.value)

        val importedTasks = newTaskRepo.getAllTasks().first()
        assertEquals(1, importedTasks.size)
        val restoredTask = importedTasks[0]
        assertEquals("Tugas Uji Ekspor", restoredTask.title)
        assertEquals("Matematika", restoredTask.subject)
        assertEquals(TaskType.KELOMPOK, restoredTask.type)
        assertEquals(TaskTag.PRAKTIKUM, restoredTask.tag)
        assertEquals("Tim Hebat", restoredTask.groupName)
        assertEquals(1, restoredTask.members.size)
        assertEquals("Budi", restoredTask.members[0].name)
        assertEquals(1, restoredTask.subtasks.size)
        assertEquals("Subtugas 1", restoredTask.subtasks[0].title)
        // Subtask should be assigned to the restored member Budi
        assertEquals(restoredTask.members[0].id, restoredTask.subtasks[0].assignedMemberId)
        assertEquals(1, restoredTask.attachments.size)
        assertEquals("sample_document.pdf", restoredTask.attachments[0].fileName)

        // Check restored attachment file exists on disk
        val restoredAttachmentFile = File(restoredTask.attachments[0].filePath)
        assertTrue(restoredAttachmentFile.exists())
        assertEquals("Test attachment content for TaskKu", restoredAttachmentFile.readText())
    }

    @Test
    fun importInvalidZip_returnsFailure() = runBlocking {
        val corruptedBytes = "This is not a zip file".toByteArray()
        val result = manager.importData(ByteArrayInputStream(corruptedBytes))
        assertTrue(result.isFailure)
    }
}
