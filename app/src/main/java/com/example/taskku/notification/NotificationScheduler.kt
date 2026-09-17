package com.example.taskku.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.ReminderOffset
import com.example.taskku.domain.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

object NotificationScheduler {

    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TASK_TITLE = "extra_task_title"
    const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"
    const val EXTRA_IS_DAILY_BRIEFING = "extra_is_daily_briefing"
    const val ACTION_DAILY_BRIEFING = "com.example.taskku.action.DAILY_BRIEFING"
    const val DAILY_BRIEFING_REQUEST_CODE = 888888
    const val DAILY_BRIEFING_NOTIFICATION_ID = 888888

    // Flexible Pre-Deadline Notification Types
    const val TYPE_ONE_DAY_19_00 = 10
    const val TYPE_THREE_HOURS_BEFORE = 11
    const val TYPE_ONE_HOUR_BEFORE = 12
    const val TYPE_ON_DEADLINE = 13
    const val TYPE_DAILY_BRIEFING = 20

    // Legacy types for compatibility
    const val TYPE_H_MINUS_3 = 0
    const val TYPE_H_MINUS_1 = 1
    const val TYPE_H_MINUS_0 = 2
    const val TYPE_OVERDUE = 3

    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            true
        }
    }

    fun calculateReminderTime(deadlineMillis: Long, offset: ReminderOffset): Long {
        return when (offset) {
            ReminderOffset.ONE_DAY_19_00 -> {
                Calendar.getInstance().apply {
                    timeInMillis = deadlineMillis
                    add(Calendar.DAY_OF_YEAR, -1)
                    set(Calendar.HOUR_OF_DAY, 19)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            ReminderOffset.THREE_HOURS_BEFORE -> deadlineMillis - (3 * 3600 * 1000L)
            ReminderOffset.ONE_HOUR_BEFORE -> deadlineMillis - (1 * 3600 * 1000L)
            ReminderOffset.ON_DEADLINE -> deadlineMillis
        }
    }

    fun getNotificationTypeForOffset(offset: ReminderOffset): Int {
        return when (offset) {
            ReminderOffset.ONE_DAY_19_00 -> TYPE_ONE_DAY_19_00
            ReminderOffset.THREE_HOURS_BEFORE -> TYPE_THREE_HOURS_BEFORE
            ReminderOffset.ONE_HOUR_BEFORE -> TYPE_ONE_HOUR_BEFORE
            ReminderOffset.ON_DEADLINE -> TYPE_ON_DEADLINE
        }
    }

    fun updateTaskNotifications(context: Context, task: Task) {
        cancelNotifications(context, task.id)
        if (task.notificationEnabled && !task.isCompleted) {
            scheduleTaskReminders(context, task)
        }
    }

    fun scheduleTaskReminders(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderTime = calculateReminderTime(task.deadlineDate, task.reminderOffset)
        val reminderType = getNotificationTypeForOffset(task.reminderOffset)

        // Schedule pre-deadline reminder
        scheduleAlarmAt(
            context = context,
            alarmManager = alarmManager,
            taskId = task.id,
            taskTitle = task.title,
            triggerTimeMillis = reminderTime,
            type = reminderType
        )

        // Also schedule at deadline if reminder offset is not already ON_DEADLINE
        if (task.reminderOffset != ReminderOffset.ON_DEADLINE) {
            scheduleAlarmAt(
                context = context,
                alarmManager = alarmManager,
                taskId = task.id,
                taskTitle = task.title,
                triggerTimeMillis = task.deadlineDate,
                type = TYPE_ON_DEADLINE
            )
        }
    }

    fun scheduleNotifications(
        context: Context,
        taskId: Long,
        taskTitle: String,
        deadlineMillis: Long,
        offset: ReminderOffset = ReminderOffset.ONE_HOUR_BEFORE
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderTime = calculateReminderTime(deadlineMillis, offset)
        val reminderType = getNotificationTypeForOffset(offset)

        scheduleAlarmAt(context, alarmManager, taskId, taskTitle, reminderTime, reminderType)
        if (offset != ReminderOffset.ON_DEADLINE) {
            scheduleAlarmAt(context, alarmManager, taskId, taskTitle, deadlineMillis, TYPE_ON_DEADLINE)
        }
    }

    private fun scheduleAlarmAt(
        context: Context,
        alarmManager: AlarmManager,
        taskId: Long,
        taskTitle: String,
        triggerTimeMillis: Long,
        type: Int
    ) {
        if (triggerTimeMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, taskTitle)
            putExtra(EXTRA_NOTIFICATION_TYPE, type)
        }
        val requestCode = (taskId * 100 + type).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canExact = canScheduleExactAlarms(context)
        try {
            if (canExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                }
            }
        } catch (e: SecurityException) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                }
            } catch (fallbackEx: Exception) {
                fallbackEx.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleDailyMorningBriefing(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val triggerCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If 06:30 today has passed or is within 1 minute from now, schedule for tomorrow
        if (triggerCal.timeInMillis <= System.currentTimeMillis() + 60_000L) {
            triggerCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DAILY_BRIEFING
            putExtra(EXTRA_IS_DAILY_BRIEFING, true)
            putExtra(EXTRA_NOTIFICATION_TYPE, TYPE_DAILY_BRIEFING)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_BRIEFING_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canExact = canScheduleExactAlarms(context)
        try {
            if (canExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                }
            }
        } catch (e: SecurityException) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                }
            } catch (fallbackEx: Exception) {
                fallbackEx.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelNotifications(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val allTypes = listOf(
            TYPE_H_MINUS_3, TYPE_H_MINUS_1, TYPE_H_MINUS_0, TYPE_OVERDUE,
            TYPE_ONE_DAY_19_00, TYPE_THREE_HOURS_BEFORE, TYPE_ONE_HOUR_BEFORE, TYPE_ON_DEADLINE
        )
        for (type in allTypes) {
            val intent = Intent(context, NotificationReceiver::class.java)
            val reqCodes = listOf((taskId * 100 + type).toInt(), (taskId * 10 + type).toInt())
            for (requestCode in reqCodes) {
                notificationManager?.cancel(requestCode)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    fun rescheduleAllNotifications(
        context: Context,
        taskRepository: TaskRepository,
        onComplete: (() -> Unit)? = null
    ) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                scheduleDailyMorningBriefing(context)

                val tasks = taskRepository.getAllTasks().first()
                tasks.forEach { task ->
                    updateTaskNotifications(context, task)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete?.invoke()
            }
        }
    }
}
