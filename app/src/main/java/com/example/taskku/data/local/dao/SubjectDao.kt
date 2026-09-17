package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE isVisible = 1")
    fun getVisibleSubjects(): Flow<List<SubjectEntity>>

    @Query("UPDATE subjects SET isVisible = :isVisible WHERE id = :id")
    suspend fun updateVisibility(id: Long, isVisible: Boolean)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)
}
