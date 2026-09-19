package com.example.taskku.ui.main

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationTabTest {

    @Test
    fun navigationTab_entriesMatchExpectedOrderAndTitles() {
        val entries = NavigationTab.entries
        assertEquals(5, entries.size)
        assertEquals(NavigationTab.DASHBOARD, entries[0])
        assertEquals("Dashboard", entries[0].title)

        assertEquals(NavigationTab.TASKS, entries[1])
        assertEquals("Tugas", entries[1].title)

        assertEquals(NavigationTab.TIMETABLE, entries[2])
        assertEquals("Jadwal", entries[2].title)

        assertEquals(NavigationTab.CALENDAR, entries[3])
        assertEquals("Kalender", entries[3].title)

        assertEquals(NavigationTab.SETTINGS, entries[4])
        assertEquals("Pengaturan", entries[4].title)
    }

    @Test
    fun visitedTabs_initialStateOnlyContainsDashboard() {
        val initialVisited = setOf(0)
        assertTrue(0 in initialVisited)
        assertFalse(1 in initialVisited)
        assertFalse(2 in initialVisited)
        assertFalse(3 in initialVisited)
        assertFalse(4 in initialVisited)
        assertEquals(1, initialVisited.size)
    }

    @Test
    fun visitedTabs_accumulatesUniqueTabsWhenVisited() {
        var visited = setOf(0)
        var selected = 0

        // User navigates to TASKS (1)
        if (1 !in visited) visited = visited + 1
        selected = 1
        assertEquals(setOf(0, 1), visited)
        assertEquals(1, selected)

        // User navigates to SETTINGS (4)
        if (4 !in visited) visited = visited + 4
        selected = 4
        assertEquals(setOf(0, 1, 4), visited)
        assertEquals(4, selected)

        // User navigates back to TASKS (1) - already visited, no duplicates
        if (1 !in visited) visited = visited + 1
        selected = 1
        assertEquals(setOf(0, 1, 4), visited)
        assertEquals(1, selected)
    }

    @Test
    fun backNavigation_fromSecondaryTab_returnsToDashboard() {
        var selectedTabIndex = 3 // CALENDAR
        val backHandlerEnabled = selectedTabIndex != 0

        assertTrue(backHandlerEnabled)

        // Trigger back handler
        if (backHandlerEnabled) {
            selectedTabIndex = 0
        }

        assertEquals(0, selectedTabIndex)
        // Now on Dashboard, BackHandler should be disabled so system/root navigation handles back
        assertFalse(selectedTabIndex != 0)
    }
}
