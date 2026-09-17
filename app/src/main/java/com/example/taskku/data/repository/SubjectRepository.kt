package com.example.taskku.data.repository

import com.example.taskku.data.local.dao.SubjectDao
import com.example.taskku.data.local.entity.SubjectEntity
import com.example.taskku.domain.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface SubjectRepository {
    suspend fun insertSubject(subject: Subject)
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subject: Subject)
    suspend fun deleteSubjectById(id: Long)
    suspend fun updateVisibility(id: Long, isVisible: Boolean)
    fun getAllSubjects(): Flow<List<Subject>>
    fun getVisibleSubjects(): Flow<List<Subject>>
}

class SubjectRepositoryImpl(
    private val subjectDao: SubjectDao
) : SubjectRepository {
    private fun SubjectEntity.toDomainModel(): Subject {
        return Subject(id, name, isPreset, isVisible)
    }

    private fun Subject.toEntity(): SubjectEntity {
        return SubjectEntity(id, name, isPreset, isVisible)
    }

    override suspend fun insertSubject(subject: Subject) {
        withContext(Dispatchers.IO) {
            subjectDao.insertSubject(subject.toEntity())
        }
    }

    override suspend fun updateSubject(subject: Subject) = withContext(Dispatchers.IO) {
        subjectDao.updateSubject(subject.toEntity())
    }

    override suspend fun deleteSubject(subject: Subject) = withContext(Dispatchers.IO) {
        subjectDao.deleteSubject(subject.toEntity())
    }

    override suspend fun deleteSubjectById(id: Long) = withContext(Dispatchers.IO) {
        subjectDao.deleteSubjectById(id)
    }

    override suspend fun updateVisibility(id: Long, isVisible: Boolean) = withContext(Dispatchers.IO) {
        subjectDao.updateVisibility(id, isVisible)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getVisibleSubjects(): Flow<List<Subject>> {
        return subjectDao.getVisibleSubjects()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }
}
