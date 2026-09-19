package com.example.taskku.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Standard window width size classes based on Material 3 guidelines:
 * - COMPACT: < 600dp (standard portrait phones)
 * - MEDIUM: 600dp - 839dp (tablets portrait, foldables unfolded, landscape phones)
 * - EXPANDED: >= 840dp (tablets landscape, desktops)
 */
enum class WindowWidthSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    val isWide: Boolean
        get() = this != COMPACT

    companion object {
        const val COMPACT_MAX_WIDTH = 600
        const val MEDIUM_MAX_WIDTH = 840

        fun fromWidth(widthDp: Int): WindowWidthSizeClass = when {
            widthDp < COMPACT_MAX_WIDTH -> COMPACT
            widthDp < MEDIUM_MAX_WIDTH -> MEDIUM
            else -> EXPANDED
        }
    }
}

/**
 * Returns the current [WindowWidthSizeClass] based on the current screen width in DP.
 */
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    return WindowWidthSizeClass.fromWidth(configuration.screenWidthDp)
}

/**
 * Returns true if the screen width is >= 600dp (Medium or Expanded).
 */
@Composable
fun isWideDisplay(): Boolean {
    return rememberWindowWidthSizeClass().isWide
}
