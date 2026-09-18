package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.TimetableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: TimetableEntity): Long

    @Update
    suspend fun updateTimetable(timetable: TimetableEntity)

    @Delete
    suspend fun deleteTimetable(timetable: TimetableEntity)

    @Query("DELETE FROM timetables WHERE id = :id")
    suspend fun deleteTimetableById(id: Long)

    @Query("SELECT * FROM timetables ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllTimetables(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetables WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getTimetablesByDay(dayOfWeek: Int): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetables WHERE id = :id")
    fun getTimetableById(id: Long): Flow<TimetableEntity?>

    @Query("SELECT * FROM timetables WHERE subject = :subject ORDER BY dayOfWeek ASC, startTime ASC")
    fun getTimetablesBySubject(subject: String): Flow<List<TimetableEntity>>
}
