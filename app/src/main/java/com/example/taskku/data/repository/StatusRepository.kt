package com.example.taskku.data.repository

import androidx.room.withTransaction
import com.example.taskku.data.local.database.AppDatabase
import com.example.taskku.data.local.dao.StatusDao
import com.example.taskku.data.local.entity.StatusEntity
import com.example.taskku.domain.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface StatusRepository {
    suspend fun insertStatus(status: Status)
    suspend fun updateStatus(status: Status)
    suspend fun deleteStatus(status: Status)
    suspend fun deleteStatusAndResetTasks(statusId: Long)
    fun getAllStatuses(): Flow<List<Status>>
}

class StatusRepositoryImpl(
    private val statusDao: StatusDao,
    private val database: AppDatabase? = null
) : StatusRepository {
    private fun StatusEntity.toDomainModel(): Status {
        return Status(id, name, colorHex, sortOrder, isDefault)
    }

    private fun Status.toEntity(): StatusEntity {
        return StatusEntity(id, name, colorHex, sortOrder, isDefault)
    }

    override suspend fun insertStatus(status: Status) {
        withContext(Dispatchers.IO) {
            statusDao.insertStatus(status.toEntity())
        }
    }

    override suspend fun updateStatus(status: Status) = withContext(Dispatchers.IO) {
        statusDao.updateStatus(status.toEntity())
    }

    override suspend fun deleteStatus(status: Status) = withContext(Dispatchers.IO) {
        statusDao.deleteStatus(status.toEntity())
    }

    override suspend fun deleteStatusAndResetTasks(statusId: Long) = withContext(Dispatchers.IO) {
        val action: suspend () -> Unit = {
            val defaultStatus = statusDao.getDefaultStatus()
            val defaultId = defaultStatus?.id ?: 1L
            statusDao.resetTaskStatusToDefault(statusId, defaultId)
            statusDao.deleteStatusById(statusId)
        }
        if (database != null) {
            database.withTransaction { action() }
        } else {
            action()
        }
    }

    override fun getAllStatuses(): Flow<List<Status>> {
        return statusDao.getAllStatuses()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }
}
