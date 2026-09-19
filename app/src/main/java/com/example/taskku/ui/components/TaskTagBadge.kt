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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskku.domain.model.TaskTag

private val COLOR_TAG_PR = Color(0xFF0984E3)
private val COLOR_TAG_KUIS = Color(0xFFE17055)
private val COLOR_TAG_PRAKTIKUM = Color(0xFF6C5CE7)
private val COLOR_TAG_PROYEK = Color(0xFF00B894)

private val CONTAINER_TAG_PR = COLOR_TAG_PR.copy(alpha = 0.15f)
private val CONTAINER_TAG_KUIS = COLOR_TAG_KUIS.copy(alpha = 0.15f)
private val CONTAINER_TAG_PRAKTIKUM = COLOR_TAG_PRAKTIKUM.copy(alpha = 0.15f)
private val CONTAINER_TAG_PROYEK = COLOR_TAG_PROYEK.copy(alpha = 0.15f)

val TaskTag.themeColor: Color
    get() = when (this) {
        TaskTag.PR -> COLOR_TAG_PR
        TaskTag.KUIS -> COLOR_TAG_KUIS
        TaskTag.PRAKTIKUM -> COLOR_TAG_PRAKTIKUM
        TaskTag.PROYEK -> COLOR_TAG_PROYEK
    }

val TaskTag.containerColor: Color
    get() = when (this) {
        TaskTag.PR -> CONTAINER_TAG_PR
        TaskTag.KUIS -> CONTAINER_TAG_KUIS
        TaskTag.PRAKTIKUM -> CONTAINER_TAG_PRAKTIKUM
        TaskTag.PROYEK -> CONTAINER_TAG_PROYEK
    }

fun getTagIcon(tag: TaskTag): ImageVector {
    return when (tag) {
        TaskTag.PR -> Icons.AutoMirrored.Outlined.Assignment
        TaskTag.KUIS -> Icons.Outlined.Quiz
        TaskTag.PRAKTIKUM -> Icons.Outlined.Science
        TaskTag.PROYEK -> Icons.Outlined.Groups
    }
}

private val TagBadgeShape = RoundedCornerShape(6.dp)

@Composable
fun TaskTagBadge(
    tag: TaskTag,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val parsedColor = tag.themeColor

    Box(
        modifier = modifier
            .clip(TagBadgeShape)
            .background(tag.containerColor)
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
