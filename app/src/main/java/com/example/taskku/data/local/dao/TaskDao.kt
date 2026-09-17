package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.*
import kotlinx.coroutines.flow.Flow

data class TaskWithDetails(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val members: List<MemberEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks: List<SubtaskEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val attachments: List<AttachmentEntity>,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val status: StatusEntity? = null
)

data class StatusCount(
    val statusId: Long,
    val statusName: String,
    val count: Int
)

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Long): Flow<TaskWithDetails?>

    @Transaction
    @Query("SELECT * FROM tasks WHERE subject = :subject")
    fun getTasksBySubject(subject: String): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE statusId = :statusId")
    fun getTasksByStatus(statusId: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE deadlineDate BETWEEN :fromDate AND :toDate")
    fun getUpcomingDeadlines(fromDate: Long, toDate: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE deadlineDate < :currentDate")
    fun getOverdueTasks(currentDate: Long): Flow<List<TaskWithDetails>>

    @Query("UPDATE tasks SET statusId = :statusId, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, statusId: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    suspend fun deleteTasksByIds(taskIds: List<Long>)

    @Query("""
        SELECT statuses.id AS statusId, statuses.name AS statusName, COUNT(tasks.id) AS count 
        FROM statuses 
        LEFT JOIN tasks ON statuses.id = tasks.statusId 
        GROUP BY statuses.id
    """)
    fun getTaskCountByStatus(): Flow<List<StatusCount>>
}
