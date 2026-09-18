package com.example.taskku.export

import android.content.Context
import com.example.taskku.data.preferences.AppPreferences
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

open class ExportImportManager(
    private val filesDir: File,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val appPreferences: AppPreferences? = null
) {
    constructor(
        context: Context,
        taskRepository: TaskRepository,
        subjectRepository: SubjectRepository,
        statusRepository: StatusRepository,
        appPreferences: AppPreferences? = null
    ) : this(context.filesDir, taskRepository, subjectRepository, statusRepository, appPreferences)

    suspend fun exportData(outputStream: OutputStream): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val tasks = taskRepository.getAllTasks().first()
            val statuses = statusRepository.getAllStatuses().first()
            val subjects = subjectRepository.getAllSubjects().first()

            val rootJson = JSONObject().apply {
                put("version", 1)
                put("timestamp", System.currentTimeMillis())

                // Statuses
                val statusArray = JSONArray()
                statuses.forEach { s ->
                    statusArray.put(JSONObject().apply {
                        put("id", s.id)
                        put("name", s.name)
                        put("colorHex", s.colorHex)
                        put("sortOrder", s.sortOrder)
                        put("isDefault", s.isDefault)
                    })
                }
                put("statuses", statusArray)

                // Subjects
                val subjectArray = JSONArray()
                subjects.forEach { sub ->
                    subjectArray.put(JSONObject().apply {
                        put("id", sub.id)
                        put("name", sub.name)
                        put("isPreset", sub.isPreset)
                        put("isVisible", sub.isVisible)
                    })
                }
                put("subjects", subjectArray)

                // Sorting preferences
                if (appPreferences != null) {
                    val prefsObj = JSONObject().apply {
                        put("sortOption", appPreferences.sortOption.value.name)
                        put("sortDirection", appPreferences.sortDirection.value.name)
                    }
                    put("preferences", prefsObj)
                }

                // Tasks
                val taskArray = JSONArray()
                tasks.forEach { t ->
                    val taskObj = JSONObject().apply {
                        put("title", t.title)
                        put("description", t.description)
                        put("subject", t.subject)
                        put("type", t.type.name)
                        put("difficulty", t.difficulty.name)
                        put("statusId", t.statusId)
                        put("deadlineDate", t.deadlineDate)
                        put("deadlineTime", t.deadlineTime)
                        put("groupName", t.groupName)
                        put("notificationEnabled", t.notificationEnabled)
                        put("reminderOffset", t.reminderOffset.name)
                        put("tag", t.tag.name)
                        put("createdAt", t.createdAt)
                        put("updatedAt", t.updatedAt)

                        // Members
                        val memberArray = JSONArray()
                        t.members.forEach { m ->
                            memberArray.put(JSONObject().apply {
                                put("name", m.name)
                            })
                        }
                        put("members", memberArray)

                        // Subtasks
                        val subtaskArray = JSONArray()
                        t.subtasks.forEach { st ->
                            val memberName = t.members.find { it.id == st.assignedMemberId }?.name
                            subtaskArray.put(JSONObject().apply {
                                put("title", st.title)
                                put("assignedMemberName", memberName ?: "")
                                put("isCompleted", st.isCompleted)
                            })
                        }
                        put("subtasks", subtaskArray)

                        // Attachments
                        val attachmentArray = JSONArray()
                        t.attachments.forEach { att ->
                            val file = File(att.filePath)
                            val zipPath = "files/${file.name}"
                            attachmentArray.put(JSONObject().apply {
                                put("fileName", att.fileName)
                                put("fileType", att.fileType)
                                put("fileSize", att.fileSize)
                                put("zipPath", zipPath)
                            })
                        }
                        put("attachments", attachmentArray)
                    }
                    taskArray.put(taskObj)
                }
                put("tasks", taskArray)
            }

            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // Write json
                zipOut.putNextEntry(ZipEntry("tasks_data.json"))
                zipOut.write(rootJson.toString(2).toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()

                // Write attachment files
                tasks.flatMap { it.attachments }.forEach { att ->
                    val file = File(att.filePath)
                    if (file.exists() && file.isFile) {
                        zipOut.putNextEntry(ZipEntry("files/${file.name}"))
                        FileInputStream(file).use { fis ->
                            fis.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    }
                }
            }

            Result.success(tasks.size)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun importData(inputStream: InputStream): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val attachmentsDir = File(filesDir, "attachments").apply {
                if (!exists()) mkdirs()
            }

            var jsonContent: String? = null
            val savedFiles = mutableMapOf<String, String>() // zipPath -> newFilePath

            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        if (entry.name == "tasks_data.json") {
                            val reader = BufferedReader(InputStreamReader(zipIn, Charsets.UTF_8))
                            jsonContent = reader.readText()
                        } else if (entry.name.startsWith("files/")) {
                            val fileName = File(entry.name).name
                            val destFile = File(attachmentsDir, "${System.currentTimeMillis()}_$fileName")
                            FileOutputStream(destFile).use { fos ->
                                zipIn.copyTo(fos)
                            }
                            savedFiles[entry.name] = destFile.absolutePath
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            val validJson = jsonContent ?: return@withContext Result.failure(IllegalStateException("Format zip tidak valid (tasks_data.json tidak ditemukan)"))

            val rootJson = JSONObject(validJson)

            // Import statuses
            val currentStatuses = statusRepository.getAllStatuses().first()
            val currentStatusNames = currentStatuses.map { it.name.lowercase() }.toSet()
            val statusArray = rootJson.optJSONArray("statuses") ?: JSONArray()
            for (i in 0 until statusArray.length()) {
                val s = statusArray.getJSONObject(i)
                val name = s.getString("name")
                if (!currentStatusNames.contains(name.lowercase())) {
                    statusRepository.insertStatus(
                        Status(
                            name = name,
                            colorHex = s.getString("colorHex"),
                            sortOrder = s.getInt("sortOrder"),
                            isDefault = s.optBoolean("isDefault", false)
                        )
                    )
                }
            }

            // Import subjects
            val currentSubjects = subjectRepository.getAllSubjects().first()
            val currentSubjectNames = currentSubjects.map { it.name.lowercase() }.toSet()
            val subjectArray = rootJson.optJSONArray("subjects") ?: JSONArray()
            for (i in 0 until subjectArray.length()) {
                val sub = subjectArray.getJSONObject(i)
                val name = sub.getString("name")
                if (!currentSubjectNames.contains(name.lowercase())) {
                    subjectRepository.insertSubject(
                        Subject(
                            name = name,
                            isPreset = sub.optBoolean("isPreset", false),
                            isVisible = sub.optBoolean("isVisible", true)
                        )
                    )
                }
            }

            // Import preferences
            val prefsObj = rootJson.optJSONObject("preferences")
            if (prefsObj != null && appPreferences != null) {
                val sortOptStr = prefsObj.optString("sortOption", "")
                val sortDirStr = prefsObj.optString("sortDirection", "")
                if (sortOptStr.isNotEmpty()) {
                    try {
                        appPreferences.setSortOption(SortOption.valueOf(sortOptStr))
                    } catch (e: Exception) {}
                }
                if (sortDirStr.isNotEmpty()) {
                    try {
                        appPreferences.setSortDirection(SortDirection.valueOf(sortDirStr))
                    } catch (e: Exception) {}
                }
            }

            // Re-fetch statuses to get actual ids
            val refreshedStatuses = statusRepository.getAllStatuses().first()
            val defaultStatusId = refreshedStatuses.find { it.isDefault }?.id ?: refreshedStatuses.firstOrNull()?.id ?: 1L

            // Import tasks
            val taskArray = rootJson.optJSONArray("tasks") ?: JSONArray()
            for (i in 0 until taskArray.length()) {
                val t = taskArray.getJSONObject(i)

                val members = mutableListOf<Member>()
                val memberArray = t.optJSONArray("members") ?: JSONArray()
                for (mIdx in 0 until memberArray.length()) {
                    val mObj = memberArray.getJSONObject(mIdx)
                    members.add(Member(name = mObj.getString("name"), taskId = 0))
                }

                val subtasks = mutableListOf<Subtask>()
                val subtaskArray = t.optJSONArray("subtasks") ?: JSONArray()
                for (stIdx in 0 until subtaskArray.length()) {
                    val stObj = subtaskArray.getJSONObject(stIdx)
                    val assignedName = stObj.optString("assignedMemberName", "")
                    val memberIdx = if (assignedName.isNotBlank()) {
                        members.indexOfFirst { it.name == assignedName }.takeIf { it >= 0 }
                    } else null
                    subtasks.add(
                        Subtask(
                            title = stObj.getString("title"),
                            taskId = 0,
                            assignedMemberId = null,
                            assignedMemberIndex = memberIdx,
                            isCompleted = stObj.optBoolean("isCompleted", false)
                        )
                    )
                }

                val attachments = mutableListOf<Attachment>()
                val attachmentArray = t.optJSONArray("attachments") ?: JSONArray()
                for (attIdx in 0 until attachmentArray.length()) {
                    val attObj = attachmentArray.getJSONObject(attIdx)
                    val zipPath = attObj.getString("zipPath")
                    val filePath = savedFiles[zipPath] ?: ""
                    attachments.add(
                        Attachment(
                            taskId = 0,
                            fileName = attObj.getString("fileName"),
                            filePath = filePath,
                            fileType = attObj.optString("fileType", "document"),
                            fileSize = attObj.optLong("fileSize", 0L)
                        )
                    )
                }

                val statusId = t.optLong("statusId", defaultStatusId)

                val task = Task(
                    title = t.getString("title"),
                    description = t.optString("description", ""),
                    subject = t.getString("subject"),
                    type = TaskType.fromString(t.optString("type", "Pribadi")),
                    difficulty = Difficulty.fromString(t.optString("difficulty", "Sedang")),
                    statusId = statusId,
                    deadlineDate = t.getLong("deadlineDate"),
                    deadlineTime = t.optString("deadlineTime", "23:59"),
                    groupName = t.optString("groupName", ""),
                    notificationEnabled = t.optBoolean("notificationEnabled", true),
                    reminderOffset = ReminderOffset.fromString(t.optString("reminderOffset", "ONE_HOUR_BEFORE")),
                    tag = TaskTag.fromString(t.optString("tag", "PR")),
                    createdAt = t.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = t.optLong("updatedAt", System.currentTimeMillis())
                )

                taskRepository.insertTaskWithDetails(task, members, subtasks, attachments)
            }

            Result.success(taskArray.length())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
