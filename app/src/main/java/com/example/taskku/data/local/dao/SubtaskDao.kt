package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.SubtaskEntity

@Dao
interface SubtaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<SubtaskEntity>)

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleCompletion(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubtasksByTaskId(taskId: Long)

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubtasksByTaskId(taskId: Long): List<SubtaskEntity>
}
