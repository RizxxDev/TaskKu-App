package com.example.taskku.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLayoutTest {

    @Test
    fun compactBreakpoint_lessThan600dp() {
        val sizeClass = WindowWidthSizeClass.fromWidth(360)
        assertEquals(WindowWidthSizeClass.COMPACT, sizeClass)
        assertFalse(sizeClass.isWide)

        val edgeSizeClass = WindowWidthSizeClass.fromWidth(599)
        assertEquals(WindowWidthSizeClass.COMPACT, edgeSizeClass)
        assertFalse(edgeSizeClass.isWide)
    }

    @Test
    fun mediumBreakpoint_between600And839dp() {
        val sizeClass600 = WindowWidthSizeClass.fromWidth(600)
        assertEquals(WindowWidthSizeClass.MEDIUM, sizeClass600)
        assertTrue(sizeClass600.isWide)

        val sizeClass800 = WindowWidthSizeClass.fromWidth(800)
        assertEquals(WindowWidthSizeClass.MEDIUM, sizeClass800)
        assertTrue(sizeClass800.isWide)

        val sizeClass839 = WindowWidthSizeClass.fromWidth(839)
        assertEquals(WindowWidthSizeClass.MEDIUM, sizeClass839)
        assertTrue(sizeClass839.isWide)
    }

    @Test
    fun expandedBreakpoint_840dpOrMore() {
        val sizeClass840 = WindowWidthSizeClass.fromWidth(840)
        assertEquals(WindowWidthSizeClass.EXPANDED, sizeClass840)
        assertTrue(sizeClass840.isWide)

        val sizeClass1200 = WindowWidthSizeClass.fromWidth(1200)
        assertEquals(WindowWidthSizeClass.EXPANDED, sizeClass1200)
        assertTrue(sizeClass1200.isWide)
    }

    @Test
    fun compactHeightBreakpoint_lessThan480dp() {
        val height360 = WindowHeightSizeClass.fromHeight(360)
        assertEquals(WindowHeightSizeClass.COMPACT, height360)
        assertTrue(height360.isCompact)

        val height479 = WindowHeightSizeClass.fromHeight(479)
        assertEquals(WindowHeightSizeClass.COMPACT, height479)
        assertTrue(height479.isCompact)
    }

    @Test
    fun mediumHeightBreakpoint_between480And899dp() {
        val height480 = WindowHeightSizeClass.fromHeight(480)
        assertEquals(WindowHeightSizeClass.MEDIUM, height480)
        assertFalse(height480.isCompact)

        val height800 = WindowHeightSizeClass.fromHeight(800)
        assertEquals(WindowHeightSizeClass.MEDIUM, height800)
        assertFalse(height800.isCompact)
    }

    @Test
    fun expandedHeightBreakpoint_900dpOrMore() {
        val height900 = WindowHeightSizeClass.fromHeight(900)
        assertEquals(WindowHeightSizeClass.EXPANDED, height900)
        assertFalse(height900.isCompact)

        val height1200 = WindowHeightSizeClass.fromHeight(1200)
        assertEquals(WindowHeightSizeClass.EXPANDED, height1200)
        assertFalse(height1200.isCompact)
    }
}
