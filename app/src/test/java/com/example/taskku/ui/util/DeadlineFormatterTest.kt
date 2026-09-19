package com.example.taskku.ui.util

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DeadlineFormatterTest {

    @Before
    fun setup() {
        DeadlineFormatter.clearCache()
    }

    @Test
    fun computeDeadlineInfo_overdueSingleDay() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        val yesterday = today.minusDays(1)
        val deadlineMillis = yesterday.atTime(23, 59).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("Terlambat 1 hari", info.text)
        assertEquals(DeadlineStatus.OVERDUE, info.status)
    }

    @Test
    fun computeDeadlineInfo_overdueMultipleDays() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        val threeDaysAgo = today.minusDays(3)
        val deadlineMillis = threeDaysAgo.atTime(23, 59).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("Terlambat 3 hari", info.text)
        assertEquals(DeadlineStatus.OVERDUE, info.status)
    }

    @Test
    fun computeDeadlineInfo_todayBeforeDeadline() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(10, 0).atZone(zone).toInstant().toEpochMilli()

        val deadlineMillis = today.atTime(14, 0).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("Hari ini", info.text)
        assertEquals(DeadlineStatus.DUE_TODAY, info.status)
    }

    @Test
    fun computeDeadlineInfo_todayPastDeadline() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(16, 0).atZone(zone).toInstant().toEpochMilli()

        val deadlineMillis = today.atTime(14, 0).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("Terlambat (hari ini)", info.text)
        assertEquals(DeadlineStatus.DUE_TODAY_OVERDUE, info.status)
    }

    @Test
    fun computeDeadlineInfo_tomorrow() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        val tomorrow = today.plusDays(1)
        val deadlineMillis = tomorrow.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("Besok", info.text)
        assertEquals(DeadlineStatus.DUE_TOMORROW, info.status)
    }

    @Test
    fun computeDeadlineInfo_futureDays() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 9, 20)
        val nowMillis = today.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        val inFiveDays = today.plusDays(5)
        val deadlineMillis = inFiveDays.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()

        val info = DeadlineFormatter.computeDeadlineInfo(deadlineMillis, nowMillis)
        assertEquals("5 hari lagi", info.text)
        assertEquals(DeadlineStatus.UPCOMING, info.status)
    }

    @Test
    fun getDeadlineInfo_cachesResultAndInvalidatesOnMidnight() {
        val zone = ZoneId.systemDefault()
        val day1 = LocalDate.of(2026, 9, 20)
        val nowDay1 = day1.atTime(10, 0).atZone(zone).toInstant().toEpochMilli()

        val deadline = day1.plusDays(2).atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        val info1 = DeadlineFormatter.getDeadlineInfo(deadline, nowDay1)
        assertEquals("2 hari lagi", info1.text)

        // Same call returns same cached instance
        val infoCached = DeadlineFormatter.getDeadlineInfo(deadline, nowDay1)
        assertEquals(info1, infoCached)

        // Next day (midnight passed)
        val day2 = LocalDate.of(2026, 9, 21)
        val nowDay2 = day2.atTime(8, 0).atZone(zone).toInstant().toEpochMilli()

        val infoNextDay = DeadlineFormatter.getDeadlineInfo(deadline, nowDay2)
        assertEquals("Besok", infoNextDay.text)
        assertEquals(DeadlineStatus.DUE_TOMORROW, infoNextDay.status)
    }
}
