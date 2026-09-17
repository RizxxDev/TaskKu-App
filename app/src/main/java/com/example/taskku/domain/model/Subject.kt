package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Subject(
    val id: Long = 0,
    val name: String,
    val isPreset: Boolean,
    val isVisible: Boolean = true
)
