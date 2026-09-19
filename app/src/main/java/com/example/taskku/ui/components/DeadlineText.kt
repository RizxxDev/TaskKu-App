package com.example.taskku.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskku.ui.util.DeadlineFormatter
import com.example.taskku.ui.util.DeadlineInfo

@Composable
fun DeadlineText(
    deadlineMillis: Long,
    modifier: Modifier = Modifier,
    deadlineInfo: DeadlineInfo? = null
) {
    val resolvedDeadlineInfo = deadlineInfo ?: remember(deadlineMillis) {
        DeadlineFormatter.getDeadlineInfo(deadlineMillis)
    }
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
    val color = remember(resolvedDeadlineInfo, defaultColor) {
        resolvedDeadlineInfo.getColor(defaultColor)
    }
    val icon = resolvedDeadlineInfo.icon

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = "Deadline",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = resolvedDeadlineInfo.text,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
