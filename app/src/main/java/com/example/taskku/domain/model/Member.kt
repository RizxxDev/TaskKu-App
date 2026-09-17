package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Member(
    val id: Long = 0,
    val taskId: Long,
    val name: String
)
