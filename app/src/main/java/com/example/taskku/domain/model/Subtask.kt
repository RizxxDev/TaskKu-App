package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Subtask(
    val id: Long = 0,
    val taskId: Long,
    val title: String,
    val assignedMemberId: Long? = null,
    val isCompleted: Boolean = false,
    val assignedMemberIndex: Int? = null
)
