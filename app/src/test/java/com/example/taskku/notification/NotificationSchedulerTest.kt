package com.example.taskku.notification

import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType
import com.example.taskku.fakes.FakeTaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSchedulerTest {

    @Test
    fun notificationConstants_areCorrect() {
        assertEquals("extra_task_id", NotificationScheduler.EXTRA_TASK_ID)
        assertEquals("extra_task_title", NotificationScheduler.EXTRA_TASK_TITLE)
        assertEquals("extra_notification_type", NotificationScheduler.EXTRA_NOTIFICATION_TYPE)
        assertEquals("extra_is_daily_briefing", NotificationScheduler.EXTRA_IS_DAILY_BRIEFING)
        assertEquals("com.example.taskku.action.DAILY_BRIEFING", NotificationScheduler.ACTION_DAILY_BRIEFING)
        assertEquals(10, NotificationScheduler.TYPE_ONE_DAY_19_00)
        assertEquals(11, NotificationScheduler.TYPE_THREE_HOURS_BEFORE)
        assertEquals(12, NotificationScheduler.TYPE_ONE_HOUR_BEFORE)
        assertEquals(13, NotificationScheduler.TYPE_ON_DEADLINE)
        assertEquals(20, NotificationScheduler.TYPE_DAILY_BRIEFING)
    }

    @Test
    fun calculateReminderTime_threeHoursBefore() {
        val deadline = 1700000000000L
        val reminder = NotificationScheduler.calculateReminderTime(
            deadline,
            com.example.taskku.domain.model.ReminderOffset.THREE_HOURS_BEFORE
        )
        assertEquals(deadline - (3 * 3600 * 1000L), reminder)
    }

    @Test
    fun calculateReminderTime_oneHourBefore() {
        val deadline = 1700000000000L
        val reminder = NotificationScheduler.calculateReminderTime(
            deadline,
            com.example.taskku.domain.model.ReminderOffset.ONE_HOUR_BEFORE
        )
        assertEquals(deadline - (1 * 3600 * 1000L), reminder)
    }

    @Test
    fun calculateReminderTime_onDeadline() {
        val deadline = 1700000000000L
        val reminder = NotificationScheduler.calculateReminderTime(
            deadline,
            com.example.taskku.domain.model.ReminderOffset.ON_DEADLINE
        )
        assertEquals(deadline, reminder)
    }

    @Test
    fun calculateReminderTime_oneDay19_00() {
        val cal = java.util.Calendar.getInstance().apply {
            set(2026, java.util.Calendar.OCTOBER, 15, 23, 59, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val deadline = cal.timeInMillis

        val reminder = NotificationScheduler.calculateReminderTime(
            deadline,
            com.example.taskku.domain.model.ReminderOffset.ONE_DAY_19_00
        )

        val reminderCal = java.util.Calendar.getInstance().apply { timeInMillis = reminder }
        assertEquals(2026, reminderCal.get(java.util.Calendar.YEAR))
        assertEquals(java.util.Calendar.OCTOBER, reminderCal.get(java.util.Calendar.MONTH))
        assertEquals(14, reminderCal.get(java.util.Calendar.DAY_OF_MONTH))
        assertEquals(19, reminderCal.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(0, reminderCal.get(java.util.Calendar.MINUTE))
    }

    @Test
    fun getNotificationTypeForOffset_mapsCorrectly() {
        assertEquals(
            NotificationScheduler.TYPE_ONE_DAY_19_00,
            NotificationScheduler.getNotificationTypeForOffset(com.example.taskku.domain.model.ReminderOffset.ONE_DAY_19_00)
        )
        assertEquals(
            NotificationScheduler.TYPE_THREE_HOURS_BEFORE,
            NotificationScheduler.getNotificationTypeForOffset(com.example.taskku.domain.model.ReminderOffset.THREE_HOURS_BEFORE)
        )
        assertEquals(
            NotificationScheduler.TYPE_ONE_HOUR_BEFORE,
            NotificationScheduler.getNotificationTypeForOffset(com.example.taskku.domain.model.ReminderOffset.ONE_HOUR_BEFORE)
        )
        assertEquals(
            NotificationScheduler.TYPE_ON_DEADLINE,
            NotificationScheduler.getNotificationTypeForOffset(com.example.taskku.domain.model.ReminderOffset.ON_DEADLINE)
        )
    }

    @Test
    fun notificationScheduling_taskPropertiesHandled() = runTest {
        val fakeRepo = FakeTaskRepository()
        val dummyTask = Task(
            id = 1L,
            title = "Test Task",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.SEDANG,
            statusId = 1L,
            statusName = "Belum Dikerjakan",
            deadlineDate = System.currentTimeMillis() + 86400000L,
            notificationEnabled = true,
            reminderOffset = com.example.taskku.domain.model.ReminderOffset.THREE_HOURS_BEFORE
        )
        fakeRepo.tasksFlow.value = listOf(dummyTask)

        assertNotNull(fakeRepo.tasksFlow.value)
        assertEquals(1, fakeRepo.tasksFlow.value.size)
        assertEquals("Test Task", fakeRepo.tasksFlow.value[0].title)
        assertEquals(com.example.taskku.domain.model.ReminderOffset.THREE_HOURS_BEFORE, fakeRepo.tasksFlow.value[0].reminderOffset)
    }
}
