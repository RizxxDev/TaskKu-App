package com.example.taskku.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskku.TaskKuApplication
import com.example.taskku.widget.TaskKuWidgetHelper

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isRebootOrUpdate = intent.action in listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )
        if (isRebootOrUpdate) {
            val applicationContext = context.applicationContext as? TaskKuApplication ?: return
            val pendingResult = goAsync()
            val taskRepository = applicationContext.container.taskRepository

            // Reschedule Daily Morning Briefing
            NotificationScheduler.scheduleDailyMorningBriefing(context)

            // Reschedule all active task reminders
            NotificationScheduler.rescheduleAllNotifications(context, taskRepository) {
                // Refresh home screen widget
                TaskKuWidgetHelper.updateWidget(context)
                pendingResult.finish()
            }
        }
    }
}
