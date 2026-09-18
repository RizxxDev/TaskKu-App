package com.example.taskku

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object Main : NavKey

@Immutable
@Serializable
data class TaskDetail(val taskId: Long) : NavKey

@Immutable
@Serializable
data class TaskForm(
    val taskId: Long? = null,
    val initialSubject: String? = null,
    val initialDeadlineDate: Long? = null,
    val initialTag: String? = null
) : NavKey
