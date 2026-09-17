package com.example.taskku.domain.model

enum class ReminderOffset(val id: String, val displayName: String) {
    ONE_DAY_19_00("1_DAY_19_00", "H-1 Hari (19.00)"),
    THREE_HOURS_BEFORE("3_HOURS_BEFORE", "H-3 Jam"),
    ONE_HOUR_BEFORE("1_HOUR_BEFORE", "H-1 Jam"),
    ON_DEADLINE("ON_DEADLINE", "Saat Deadline");

    companion object {
        fun fromString(value: String?): ReminderOffset {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) 
            } ?: ONE_HOUR_BEFORE
        }
    }
}
