package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

enum class SchoolDay(val dayOfWeek: Int, val displayName: String, val shortName: String) {
    SENIN(1, "Senin", "Sen"),
    SELASA(2, "Selasa", "Sel"),
    RABU(3, "Rabu", "Rab"),
    KAMIS(4, "Kamis", "Kam"),
    JUMAT(5, "Jumat", "Jum"),
    SABTU(6, "Sabtu", "Sab"),
    MINGGU(7, "Minggu", "Min");

    companion object {
        fun fromDayOfWeek(day: Int): SchoolDay =
            entries.find { it.dayOfWeek == day } ?: SENIN

        fun currentDayOfWeek(): Int =
            LocalDate.now().dayOfWeek.value
    }
}

@Immutable
data class TimetableItem(
    val id: Long = 0,
    val subject: String,
    val dayOfWeek: Int, // 1 = Senin, ..., 7 = Minggu
    val startTime: String, // "08:45"
    val endTime: String, // "10:15"
    val room: String = "",
    val teacher: String = ""
) {
    val dayName: String
        get() = SchoolDay.fromDayOfWeek(dayOfWeek).displayName

    val timeRange: String = "$startTime - $endTime"

    fun isCurrentlyActive(): Boolean {
        val currentDay = SchoolDay.currentDayOfWeek()
        if (currentDay != dayOfWeek) return false

        val now = LocalTime.now()
        val nowMinutes = now.hour * 60 + now.minute
        val startMinutes = parseMinutes(startTime)
        val endMinutes = parseMinutes(endTime)

        return nowMinutes in startMinutes..endMinutes
    }
}

private fun parseMinutes(timeStr: String): Int {
    val colonIdx = timeStr.indexOf(':')
    if (colonIdx <= 0) return -1
    val h = timeStr.substring(0, colonIdx).toIntOrNull() ?: return -1
    val m = timeStr.substring(colonIdx + 1).toIntOrNull() ?: return -1
    return h * 60 + m
}
