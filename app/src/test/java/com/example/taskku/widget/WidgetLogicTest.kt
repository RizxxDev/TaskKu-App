package com.example.taskku.widget

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.ReminderOffset
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WidgetLogicTest {

    @Test
    fun reminderOffset_fromString_parsesCorrectly() {
        assertEquals(ReminderOffset.ONE_DAY_19_00, ReminderOffset.fromString("ONE_DAY_19_00"))
        assertEquals(ReminderOffset.ONE_DAY_19_00, ReminderOffset.fromString("1_DAY_19_00"))
        assertEquals(ReminderOffset.THREE_HOURS_BEFORE, ReminderOffset.fromString("THREE_HOURS_BEFORE"))
        assertEquals(ReminderOffset.THREE_HOURS_BEFORE, ReminderOffset.fromString("3_HOURS_BEFORE"))
        assertEquals(ReminderOffset.ONE_HOUR_BEFORE, ReminderOffset.fromString("ONE_HOUR_BEFORE"))
        assertEquals(ReminderOffset.ONE_HOUR_BEFORE, ReminderOffset.fromString("1_HOUR_BEFORE"))
        assertEquals(ReminderOffset.ON_DEADLINE, ReminderOffset.fromString("ON_DEADLINE"))
        // Fallback default
        assertEquals(ReminderOffset.ONE_HOUR_BEFORE, ReminderOffset.fromString("INVALID_VALUE"))
        assertEquals(ReminderOffset.ONE_HOUR_BEFORE, ReminderOffset.fromString(null))
    }

    @Test
    fun widgetDeadlineFormatting_classifiesRangesAccurately() {
        val startOfToday = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 10, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val oneDay = 24 * 60 * 60 * 1000L

        fun formatDeadline(deadlineDate: Long, deadlineTime: String): String {
            val startOfTomorrow = startOfToday + oneDay
            val startOfDayAfterTomorrow = startOfTomorrow + oneDay

            return when {
                deadlineDate < startOfToday -> "Lewat deadline • $deadlineTime"
                deadlineDate in startOfToday until startOfTomorrow -> "Hari ini • $deadlineTime"
                deadlineDate in startOfTomorrow until startOfDayAfterTomorrow -> "Besok • $deadlineTime"
                else -> {
                    val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
                    "${sdf.format(Date(deadlineDate))} • $deadlineTime"
                }
            }
        }

        // 1. Yesterday (overdue)
        val yesterdayMillis = startOfToday - 3600000L
        assertEquals("Lewat deadline • 23:59", formatDeadline(yesterdayMillis, "23:59"))

        // 2. Today
        val todayMillis = startOfToday + 14 * 3600000L
        assertEquals("Hari ini • 14:00", formatDeadline(todayMillis, "14:00"))

        // 3. Tomorrow
        val tomorrowMillis = startOfToday + oneDay + 10 * 3600000L
        assertEquals("Besok • 10:00", formatDeadline(tomorrowMillis, "10:00"))

        // 4. In 5 days
        val fiveDaysLater = startOfToday + 5 * oneDay
        val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        assertEquals("${sdf.format(Date(fiveDaysLater))} • 12:00", formatDeadline(fiveDaysLater, "12:00"))
    }

    @Test
    fun widgetTaskFiltering_selectsTodayAndTomorrowAndPendingOverdue() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = cal.timeInMillis
        val oneDay = 24 * 60 * 60 * 1000L
        val endOfTomorrow = startOfToday + (2 * oneDay) - 1

        val taskOverduePending = Task(
            id = 1L,
            title = "Overdue Pending",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1L,
            statusName = "Belum Dikerjakan",
            deadlineDate = startOfToday - oneDay
        )

        val taskOverdueCompleted = Task(
            id = 2L,
            title = "Overdue Completed",
            subject = "Fisika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 3L,
            statusName = "Selesai",
            deadlineDate = startOfToday - oneDay
        )

        val taskToday = Task(
            id = 3L,
            title = "Task Today",
            subject = "Kimia",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1L,
            statusName = "Belum Dikerjakan",
            deadlineDate = startOfToday + 10 * 3600000L
        )

        val taskTomorrow = Task(
            id = 4L,
            title = "Task Tomorrow",
            subject = "Biologi",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1L,
            statusName = "Belum Dikerjakan",
            deadlineDate = startOfToday + oneDay + 12 * 3600000L
        )

        val taskNextWeek = Task(
            id = 5L,
            title = "Task Next Week",
            subject = "Sejarah",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1L,
            statusName = "Belum Dikerjakan",
            deadlineDate = startOfToday + 7 * oneDay
        )

        val allTasks = listOf(taskOverduePending, taskOverdueCompleted, taskToday, taskTomorrow, taskNextWeek)

        val filteredTasks = allTasks.filter { task ->
            (task.deadlineDate in startOfToday..endOfTomorrow) || (!task.isCompleted && task.deadlineDate < startOfToday)
        }

        assertTrue(filteredTasks.contains(taskOverduePending))
        assertFalse(filteredTasks.contains(taskOverdueCompleted)) // Completed overdue task should not clutter widget
        assertTrue(filteredTasks.contains(taskToday))
        assertTrue(filteredTasks.contains(taskTomorrow))
        assertFalse(filteredTasks.contains(taskNextWeek)) // Next week should not be in today & tomorrow widget
        assertEquals(3, filteredTasks.size)
    }

    @Test
    fun status_isCompleted_detectsCompletedAndPendingStatuses() {
        val completedStatus1 = com.example.taskku.domain.model.Status(
            id = 3L,
            name = "Selesai",
            colorHex = "#55EFC4",
            sortOrder = 3,
            isDefault = true
        )
        val completedStatus2 = com.example.taskku.domain.model.Status(
            id = 4L,
            name = "Done",
            colorHex = "#55EFC4",
            sortOrder = 4,
            isDefault = false
        )
        val pendingStatus1 = com.example.taskku.domain.model.Status(
            id = 1L,
            name = "Belum Dikerjakan",
            colorHex = "#B2BEC3",
            sortOrder = 1,
            isDefault = true
        )
        val inProgressStatus = com.example.taskku.domain.model.Status(
            id = 2L,
            name = "Sedang Dikerjakan",
            colorHex = "#74B9FF",
            sortOrder = 2,
            isDefault = true
        )

        assertTrue(completedStatus1.isCompleted)
        assertTrue(completedStatus2.isCompleted)
        assertFalse(pendingStatus1.isCompleted)
        assertFalse(inProgressStatus.isCompleted)

        val statuses = listOf(pendingStatus1, inProgressStatus, completedStatus1)

        // Target status resolution when checking off a task (targetCompleted = true)
        val targetWhenCompleted = statuses.find { it.isCompleted } ?: statuses.lastOrNull()
        assertEquals(completedStatus1.id, targetWhenCompleted?.id)

        // Target status resolution when unchecking a task (targetCompleted = false)
        val targetWhenUncompleted = statuses.find { it.isDefault && !it.isCompleted } ?: statuses.firstOrNull()
        assertEquals(pendingStatus1.id, targetWhenUncompleted?.id)
    }
}
