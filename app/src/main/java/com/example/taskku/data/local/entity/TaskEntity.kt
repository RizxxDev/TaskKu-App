package com.example.taskku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["statusId"]),
        Index(value = ["deadlineDate"]),
        Index(value = ["subject"]),
        Index(value = ["tag"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val subject: String,
    val type: String, // "pribadi" or "kelompok"
    val difficulty: String, // "mudah", "sedang", "sulit"
    val statusId: Long,
    val deadlineDate: Long, // epoch millis
    val deadlineTime: String = "23:59", // HH:mm
    val groupName: String = "",
    val notificationEnabled: Boolean = true,
    val reminderOffset: String = com.example.taskku.domain.model.ReminderOffset.ONE_HOUR_BEFORE.name,
    val tag: String = com.example.taskku.domain.model.TaskTag.PR.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
