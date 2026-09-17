package com.example.taskku.domain.model

enum class TaskType(val displayName: String) {
    PRIBADI("Pribadi"),
    KELOMPOK("Kelompok");

    companion object {
        fun fromString(value: String): TaskType = 
            values().find { it.name.equals(value, ignoreCase = true) } ?: PRIBADI
    }
}
