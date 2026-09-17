package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Status(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val sortOrder: Int,
    val isDefault: Boolean
) {
    val isCompleted: Boolean
        get() = name.equals("Selesai", ignoreCase = true) || name.equals("Done", ignoreCase = true)
}
