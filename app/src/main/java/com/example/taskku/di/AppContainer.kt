package com.example.taskku.di

import android.content.Context
import com.example.taskku.data.local.database.AppDatabase
import com.example.taskku.data.repository.TaskRepositoryImpl
import com.example.taskku.data.repository.SubjectRepositoryImpl
import com.example.taskku.data.repository.StatusRepositoryImpl
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.StatusRepository

class AppContainer(context: Context) {
    private val database by lazy { AppDatabase.getInstance(context) }
    
    private val taskDao by lazy { database.taskDao() }
    private val memberDao by lazy { database.memberDao() }
    private val subtaskDao by lazy { database.subtaskDao() }
    private val attachmentDao by lazy { database.attachmentDao() }
    private val statusDao by lazy { database.statusDao() }
    private val subjectDao by lazy { database.subjectDao() }
    
    val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(taskDao, memberDao, subtaskDao, attachmentDao, database, context) }
    val subjectRepository: SubjectRepository by lazy { SubjectRepositoryImpl(subjectDao) }
    val statusRepository: StatusRepository by lazy { StatusRepositoryImpl(statusDao, database) }
    val appPreferences: com.example.taskku.data.preferences.AppPreferences by lazy { com.example.taskku.data.preferences.AppPreferences(context) }
    val exportImportManager: com.example.taskku.export.ExportImportManager by lazy { com.example.taskku.export.ExportImportManager(context, taskRepository, subjectRepository, statusRepository, appPreferences) }
}
