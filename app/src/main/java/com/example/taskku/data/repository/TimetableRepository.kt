package com.example.taskku.data.repository

import android.content.Context
import com.example.taskku.data.local.dao.TimetableDao
import com.example.taskku.data.local.entity.TimetableEntity
import com.example.taskku.domain.model.SchoolDay
import com.example.taskku.domain.model.TimetableItem
import com.example.taskku.widget.TaskKuWidgetHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

interface TimetableRepository {
    suspend fun insertTimetable(item: TimetableItem): Long
    suspend fun updateTimetable(item: TimetableItem)
    suspend fun deleteTimetable(item: TimetableItem)
    suspend fun deleteTimetableById(id: Long)
    fun getAllTimetables(): Flow<List<TimetableItem>>
    fun getTimetablesByDay(dayOfWeek: Int): Flow<List<TimetableItem>>
    fun getTimetableById(id: Long): Flow<TimetableItem?>
    fun getUpcomingNextClass(): Flow<TimetableItem?>
}

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao,
    private val context: Context? = null
) : TimetableRepository {

    private fun TimetableEntity.toDomainModel(): TimetableItem {
        return TimetableItem(
            id = id,
            subject = subject,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            room = room,
            teacher = teacher
        )
    }

    private fun TimetableItem.toEntity(): TimetableEntity {
        return TimetableEntity(
            id = id,
            subject = subject,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            room = room,
            teacher = teacher
        )
    }

    override suspend fun insertTimetable(item: TimetableItem): Long = withContext(Dispatchers.IO) {
        val id = timetableDao.insertTimetable(item.toEntity())
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
        id
    }

    override suspend fun updateTimetable(item: TimetableItem): Unit = withContext(Dispatchers.IO) {
        timetableDao.updateTimetable(item.toEntity())
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
    }

    override suspend fun deleteTimetable(item: TimetableItem): Unit = withContext(Dispatchers.IO) {
        timetableDao.deleteTimetable(item.toEntity())
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
    }

    override suspend fun deleteTimetableById(id: Long): Unit = withContext(Dispatchers.IO) {
        timetableDao.deleteTimetableById(id)
        context?.let { TaskKuWidgetHelper.updateWidget(it) }
    }

    override fun getAllTimetables(): Flow<List<TimetableItem>> {
        return timetableDao.getAllTimetables()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTimetablesByDay(dayOfWeek: Int): Flow<List<TimetableItem>> {
        return timetableDao.getTimetablesByDay(dayOfWeek)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTimetableById(id: Long): Flow<TimetableItem?> {
        return timetableDao.getTimetableById(id)
            .map { it?.toDomainModel() }
            .flowOn(Dispatchers.IO)
    }

    override fun getUpcomingNextClass(): Flow<TimetableItem?> {
        return timetableDao.getAllTimetables()
            .map { list ->
                val currentDay = SchoolDay.currentDayOfWeek()
                val now = java.time.LocalTime.now()
                val currentTime = String.format(Locale.ROOT, "%02d:%02d", now.hour, now.minute)

                list.filter { it.dayOfWeek == currentDay }
                    .map { it.toDomainModel() }
                    .sortedBy { it.startTime }
                    .firstOrNull { it.endTime > currentTime }
            }
            .flowOn(Dispatchers.IO)
    }
}
