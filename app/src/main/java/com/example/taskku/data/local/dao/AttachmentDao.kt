package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.AttachmentEntity

@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    @Update
    suspend fun updateAttachment(attachment: AttachmentEntity)

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Query("DELETE FROM attachments WHERE taskId = :taskId")
    suspend fun deleteAttachmentsByTaskId(taskId: Long)

    @Query("SELECT * FROM attachments WHERE taskId = :taskId")
    suspend fun getAttachmentsByTaskId(taskId: Long): List<AttachmentEntity>
}
