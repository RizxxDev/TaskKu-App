package com.example.taskku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskku.domain.model.TaskTag

val TaskTag.themeColor: Color
    get() = when (this) {
        TaskTag.PR -> Color(0xFF0984E3)
        TaskTag.KUIS -> Color(0xFFE17055)
        TaskTag.PRAKTIKUM -> Color(0xFF6C5CE7)
        TaskTag.PROYEK -> Color(0xFF00B894)
    }

fun getTagIcon(tag: TaskTag): ImageVector {
    return when (tag) {
        TaskTag.PR -> Icons.AutoMirrored.Outlined.Assignment
        TaskTag.KUIS -> Icons.Outlined.Quiz
        TaskTag.PRAKTIKUM -> Icons.Outlined.Science
        TaskTag.PROYEK -> Icons.Outlined.Groups
    }
}

@Composable
fun TaskTagBadge(
    tag: TaskTag,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val parsedColor = tag.themeColor


    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(parsedColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showIcon) {
                Icon(
                    imageVector = getTagIcon(tag),
                    contentDescription = tag.displayName,
                    tint = parsedColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = tag.displayName,
                color = parsedColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
