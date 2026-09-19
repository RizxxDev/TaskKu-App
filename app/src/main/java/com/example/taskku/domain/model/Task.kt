package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable
import java.util.concurrent.TimeUnit

@Immutable
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subject: String,
    val type: TaskType,
    val difficulty: Difficulty,
    val statusId: Long,
    val statusName: String = "Belum Dikerjakan",
    val statusColorHex: String = "#B2BEC3",
    val deadlineDate: Long = System.currentTimeMillis() + 86400000L, // epoch millis
    val deadlineTime: String = "23:59", // HH:mm
    val groupName: String = "",
    val notificationEnabled: Boolean = true,
    val reminderOffset: ReminderOffset = ReminderOffset.ONE_HOUR_BEFORE,
    val tag: TaskTag = TaskTag.PR,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val members: List<Member> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val directMemberCount: Int = 0
) {
    val deadlineMillis: Long
        get() = deadlineDate

    val memberCount: Int
        get() = if (members.isNotEmpty()) members.size else directMemberCount

    val isOverdue: Boolean
        get() = deadlineDate < System.currentTimeMillis()

    val daysUntilDeadline: Long
        get() {
            val diff = deadlineDate - System.currentTimeMillis()
            return TimeUnit.MILLISECONDS.toDays(diff)
        }

    val isUrgent: Boolean
        get() = !isOverdue && daysUntilDeadline <= 3

    val isCompleted: Boolean
        get() = statusName.equals("Selesai", ignoreCase = true) || statusName.equals("Done", ignoreCase = true)
}
