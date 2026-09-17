package com.example.taskku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DifficultyBadge(
    difficulty: com.example.taskku.domain.model.Difficulty,
    modifier: Modifier = Modifier
) {
    val (color, text) = remember(difficulty) {
        when (difficulty) {
            com.example.taskku.domain.model.Difficulty.MUDAH -> com.example.taskku.theme.DifficultyEasy to "★☆☆"
            com.example.taskku.domain.model.Difficulty.SEDANG -> com.example.taskku.theme.DifficultyMedium to "★★☆"
            com.example.taskku.domain.model.Difficulty.SULIT -> com.example.taskku.theme.DifficultyHard to "★★★"
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
