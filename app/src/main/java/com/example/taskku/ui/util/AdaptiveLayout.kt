package com.example.taskku.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * Standard window height size classes based on Material 3 guidelines:
 * - COMPACT: < 480dp (landscape phones)
 * - MEDIUM: 480dp - 899dp (portrait phones, small tablets)
 * - EXPANDED: >= 900dp (tablets, foldables unfolded portrait)
 */
enum class WindowHeightSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    val isCompact: Boolean
        get() = this == COMPACT

    companion object {
        const val COMPACT_MAX_HEIGHT = 480
        const val MEDIUM_MAX_HEIGHT = 900

        fun fromHeight(heightDp: Int): WindowHeightSizeClass = when {
            heightDp < COMPACT_MAX_HEIGHT -> COMPACT
            heightDp < MEDIUM_MAX_HEIGHT -> MEDIUM
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
    return remember(configuration.screenWidthDp) {
        WindowWidthSizeClass.fromWidth(configuration.screenWidthDp)
    }
}

/**
 * Returns the current [WindowHeightSizeClass] based on the current screen height in DP.
 */
@Composable
fun rememberWindowHeightSizeClass(): WindowHeightSizeClass {
    val configuration = LocalConfiguration.current
    return remember(configuration.screenHeightDp) {
        WindowHeightSizeClass.fromHeight(configuration.screenHeightDp)
    }
}

/**
 * Returns true if the screen width is >= 600dp (Medium or Expanded).
 */
@Composable
fun isWideDisplay(): Boolean {
    return rememberWindowWidthSizeClass().isWide
}

/**
 * Returns true if the screen height is < 480dp (Compact height, typical for landscape phones).
 */
@Composable
fun isCompactHeight(): Boolean {
    return rememberWindowHeightSizeClass().isCompact
}
