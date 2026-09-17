package com.example.taskku.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskku.theme.CalendarOverdue
import com.example.taskku.theme.CalendarUrgent
import com.example.taskku.theme.CalendarUpcoming

@Composable
fun DeadlineText(
    deadlineMillis: Long,
    modifier: Modifier = Modifier
) {
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
    val (text, color, icon) = remember(deadlineMillis, defaultColor) {
        val deadlineDate = java.time.Instant.ofEpochMilli(deadlineMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        val today = java.time.LocalDate.now()
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, deadlineDate)
        val now = System.currentTimeMillis()

        when {
            daysBetween < 0 -> {
                val overdueDays = -daysBetween
                val label = if (overdueDays == 1L) "Terlambat 1 hari" else "Terlambat $overdueDays hari"
                Triple(label, CalendarOverdue, Icons.Filled.Warning)
            }
            daysBetween == 0L -> {
                if (deadlineMillis < now) {
                    Triple("Terlambat (hari ini)", CalendarOverdue, Icons.Filled.Warning)
                } else {
                    Triple("Hari ini", CalendarUrgent, Icons.Filled.Warning)
                }
            }
            daysBetween == 1L -> Triple("Besok", CalendarUpcoming, Icons.Outlined.DateRange)
            else -> Triple("$daysBetween hari lagi", defaultColor, Icons.Outlined.DateRange)
        }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = "Deadline",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
