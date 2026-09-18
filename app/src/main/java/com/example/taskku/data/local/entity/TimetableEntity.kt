package com.example.taskku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timetables",
    indices = [
        Index(value = ["dayOfWeek"]),
        Index(value = ["subject"])
    ]
)
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val dayOfWeek: Int, // 1 = Senin, 2 = Selasa, ..., 6 = Sabtu, 7 = Minggu
    val startTime: String, // "HH:mm"
    val endTime: String, // "HH:mm"
    val room: String = "",
    val teacher: String = ""
)
