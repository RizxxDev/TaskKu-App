package com.example.taskku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskku.domain.model.Difficulty
import com.example.taskku.theme.DifficultyEasy
import com.example.taskku.theme.DifficultyHard
import com.example.taskku.theme.DifficultyMedium

private const val STARS_MUDAH = "★☆☆"
private const val STARS_SEDANG = "★★☆"
private const val STARS_SULIT = "★★★"

private val BG_MUDAH = DifficultyEasy.copy(alpha = 0.15f)
private val BG_SEDANG = DifficultyMedium.copy(alpha = 0.15f)
private val BG_SULIT = DifficultyHard.copy(alpha = 0.15f)

val Difficulty.stars: String
    get() = when (this) {
        Difficulty.MUDAH -> STARS_MUDAH
        Difficulty.SEDANG -> STARS_SEDANG
        Difficulty.SULIT -> STARS_SULIT
    }

val Difficulty.badgeColor: Color
    get() = when (this) {
        Difficulty.MUDAH -> DifficultyEasy
        Difficulty.SEDANG -> DifficultyMedium
        Difficulty.SULIT -> DifficultyHard
    }

val Difficulty.badgeContainerColor: Color
    get() = when (this) {
        Difficulty.MUDAH -> BG_MUDAH
        Difficulty.SEDANG -> BG_SEDANG
        Difficulty.SULIT -> BG_SULIT
    }

private val BadgeShape = RoundedCornerShape(4.dp)

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(BadgeShape)
            .background(difficulty.badgeContainerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = difficulty.stars,
            color = difficulty.badgeColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
