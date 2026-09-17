package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Attachment(
    val id: Long = 0,
    val taskId: Long,
    val fileName: String,
    val filePath: String,
    val fileType: String,
    val fileSize: Long,
    val addedAt: Long = System.currentTimeMillis()
)
