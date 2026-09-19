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
}
