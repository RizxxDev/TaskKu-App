package com.example.taskku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.concurrent.ConcurrentHashMap

private val statusColorCache = ConcurrentHashMap<String, Color>()
private val statusContainerColorCache = ConcurrentHashMap<String, Color>()

fun parseStatusColor(colorHex: String): Color {
    return statusColorCache.computeIfAbsent(colorHex) { hex ->
        try {
            val clean = if (hex.startsWith("#")) hex.substring(1) else hex
            when (clean.length) {
                6 -> Color(0xFF000000L or clean.toLong(16))
                8 -> Color(clean.toLong(16))
                else -> Color.Gray
            }
        } catch (e: Exception) {
            Color.Gray
        }
    }
}

fun parseStatusContainerColor(colorHex: String): Color {
    return statusContainerColorCache.computeIfAbsent(colorHex) { hex ->
        parseStatusColor(hex).copy(alpha = 0.2f)
    }
}

@Composable
fun StatusBadge(
    statusName: String,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val parsedColor = parseStatusColor(colorHex)
    val containerColor = parseStatusContainerColor(colorHex)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusName,
            color = parsedColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
