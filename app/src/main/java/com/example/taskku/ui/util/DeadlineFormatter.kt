package com.example.taskku.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.taskku.theme.CalendarOverdue
import com.example.taskku.theme.CalendarUpcoming
import com.example.taskku.theme.CalendarUrgent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

enum class DeadlineStatus {
    OVERDUE,
    DUE_TODAY_OVERDUE,
    DUE_TODAY,
    DUE_TOMORROW,
    UPCOMING
}

@Immutable
data class DeadlineInfo(
    val text: String,
    val status: DeadlineStatus
) {
    fun getColor(defaultColor: Color): Color = when (status) {
        DeadlineStatus.OVERDUE, DeadlineStatus.DUE_TODAY_OVERDUE -> CalendarOverdue
        DeadlineStatus.DUE_TODAY -> CalendarUrgent
        DeadlineStatus.DUE_TOMORROW -> CalendarUpcoming
        DeadlineStatus.UPCOMING -> defaultColor
    }

    val icon: ImageVector
        get() = when (status) {
            DeadlineStatus.OVERDUE, DeadlineStatus.DUE_TODAY_OVERDUE, DeadlineStatus.DUE_TODAY -> Icons.Filled.Warning
            DeadlineStatus.DUE_TOMORROW, DeadlineStatus.UPCOMING -> Icons.Outlined.DateRange
        }
}

object DeadlineFormatter {
    private val cache = ConcurrentHashMap<Long, DeadlineInfo>()

    @Volatile
    private var cachedEpochDay: Long = -1L

    /**
     * Retrieve precomputed or cached DeadlineInfo for [deadlineMillis].
     * Avoids date math and object allocation inside Composable render passes.
     */
    fun getDeadlineInfo(
        deadlineMillis: Long,
        now: Long = System.currentTimeMillis()
    ): DeadlineInfo {
        val currentEpochDay = now / 86_400_000L
        if (currentEpochDay != cachedEpochDay) {
            cache.clear()
            cachedEpochDay = currentEpochDay
        }

        val cached = cache[deadlineMillis]
        if (cached != null) return cached

        val computed = computeDeadlineInfo(deadlineMillis, now)
        cache[deadlineMillis] = computed
        return computed
    }

    fun computeDeadlineInfo(deadlineMillis: Long, now: Long): DeadlineInfo {
        val deadlineDate = Instant.ofEpochMilli(deadlineMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = Instant.ofEpochMilli(now)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val daysBetween = ChronoUnit.DAYS.between(today, deadlineDate)

        return when {
            daysBetween < 0 -> {
                val overdueDays = -daysBetween
                val label = if (overdueDays == 1L) "Terlambat 1 hari" else "Terlambat $overdueDays hari"
                DeadlineInfo(label, DeadlineStatus.OVERDUE)
            }
            daysBetween == 0L -> {
                if (deadlineMillis < now) {
                    DeadlineInfo("Terlambat (hari ini)", DeadlineStatus.DUE_TODAY_OVERDUE)
                } else {
                    DeadlineInfo("Hari ini", DeadlineStatus.DUE_TODAY)
                }
            }
            daysBetween == 1L -> DeadlineInfo("Besok", DeadlineStatus.DUE_TOMORROW)
            else -> DeadlineInfo("$daysBetween hari lagi", DeadlineStatus.UPCOMING)
        }
    }

    fun clearCache() {
        cache.clear()
        cachedEpochDay = -1L
    }
}
