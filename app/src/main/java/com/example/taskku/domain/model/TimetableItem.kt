package com.example.taskku.domain.model

import androidx.compose.runtime.Immutable
import java.util.Calendar
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

        fun currentDayOfWeek(): Int {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 1
            }
        }
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

    val timeRange: String
        get() = "$startTime - $endTime"

    fun isCurrentlyActive(): Boolean {
        val cal = Calendar.getInstance()
        val currentDay = SchoolDay.currentDayOfWeek()
        if (currentDay != dayOfWeek) return false

        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMin = cal.get(Calendar.MINUTE)
        val nowFormatted = String.format(Locale.ROOT, "%02d:%02d", currentHour, currentMin)

        return nowFormatted in startTime..endTime
    }
}
