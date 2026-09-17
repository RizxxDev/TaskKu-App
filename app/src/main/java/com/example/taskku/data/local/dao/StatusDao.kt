package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.StatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: StatusEntity): Long

    @Update
    suspend fun updateStatus(status: StatusEntity)

    @Delete
    suspend fun deleteStatus(status: StatusEntity)

    @Query("SELECT * FROM statuses ORDER BY sortOrder ASC")
    fun getAllStatuses(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE isDefault = 1 ORDER BY sortOrder ASC LIMIT 1")
    suspend fun getDefaultStatus(): StatusEntity?

    @Query("SELECT * FROM statuses WHERE id = :id")
    suspend fun getStatusById(id: Long): StatusEntity?

    @Query("UPDATE tasks SET statusId = :defaultStatusId WHERE statusId = :statusId")
    suspend fun resetTaskStatusToDefault(statusId: Long, defaultStatusId: Long)

    @Query("DELETE FROM statuses WHERE id = :id")
    suspend fun deleteStatusById(id: Long)
}
