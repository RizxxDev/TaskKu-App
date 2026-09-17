package com.example.taskku.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.taskku.MainActivity
import com.example.taskku.R
import com.example.taskku.TaskKuApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isDailyBriefing = intent.getBooleanExtra(NotificationScheduler.EXTRA_IS_DAILY_BRIEFING, false)
            || intent.action == NotificationScheduler.ACTION_DAILY_BRIEFING

        if (isDailyBriefing) {
            handleDailyBriefing(context)
            return
        }

        val taskId = intent.getLongExtra(NotificationScheduler.EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(NotificationScheduler.EXTRA_TASK_TITLE) ?: "Tugas"
        val type = intent.getIntExtra(NotificationScheduler.EXTRA_NOTIFICATION_TYPE, -1)

        if (taskId == -1L || type == -1) return

        handleTaskReminder(context, taskId, taskTitle, type)
    }

    private fun handleDailyBriefing(context: Context) {
        // Reschedule for tomorrow morning
        NotificationScheduler.scheduleDailyMorningBriefing(context)

        // Permission check on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }

        val app = context.applicationContext as? TaskKuApplication
        val taskRepository = app?.container?.taskRepository ?: return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val calStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val calEnd = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }

                val allTasks = taskRepository.getAllTasks().first()
                val todayTasks = allTasks.filter { task ->
                    !task.isCompleted && task.deadlineDate in calStart.timeInMillis..calEnd.timeInMillis
                }

                if (todayTasks.isNotEmpty()) {
                    val count = todayTasks.size
                    val message = if (count == 1) {
                        "Hari ini ada 1 tugas yang harus dikumpulkan!"
                    } else {
                        "Hari ini ada $count tugas yang harus dikumpulkan!"
                    }

                    val bigText = buildString {
                        append(message)
                        append("\n")
                        todayTasks.take(3).forEach { t ->
                            append("\n• ${t.title} (${t.subject}) - ${t.deadlineTime}")
                        }
                        if (todayTasks.size > 3) {
                            append("\n• dan ${todayTasks.size - 3} tugas lainnya")
                        }
                    }

                    val contentIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        NotificationScheduler.DAILY_BRIEFING_REQUEST_CODE,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val iconRes = R.drawable.ic_notification

                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notification = NotificationCompat.Builder(context, TaskKuApplication.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(iconRes)
                        .setContentTitle("Daily Morning Briefing")
                        .setContentText(message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(NotificationScheduler.DAILY_BRIEFING_NOTIFICATION_ID, notification)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleTaskReminder(context: Context, taskId: Long, taskTitle: String, type: Int) {
        // On Android 13+ (API 33+), check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        // Check whether notifications are enabled in system settings
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }

        val app = context.applicationContext as? TaskKuApplication
        val taskRepository = app?.container?.taskRepository

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (taskRepository != null) {
                    val task = taskRepository.getTaskById(taskId).firstOrNull()
                    // If task was deleted, marked completed, or reminder disabled, do not show notification
                    if (task == null || task.isCompleted || !task.notificationEnabled) {
                        NotificationScheduler.cancelNotifications(context, taskId)
                        return@launch
                    }
                }

                val message = when (type) {
                    NotificationScheduler.TYPE_ONE_DAY_19_00 -> "🌙 Besok deadline tugas '$taskTitle'! Jangan lupa diselesaikan malam ini."
                    NotificationScheduler.TYPE_THREE_HOURS_BEFORE -> "⏰ 3 jam lagi! Tugas '$taskTitle' harus segera dikumpulkan."
                    NotificationScheduler.TYPE_ONE_HOUR_BEFORE -> "🚨 1 jam lagi! Deadline tugas '$taskTitle' sudah sangat dekat."
                    NotificationScheduler.TYPE_ON_DEADLINE -> "⚠️ Waktu habis! Batas pengumpulan tugas '$taskTitle' sekarang."
                    0 -> "⚠️ Tugas '$taskTitle' deadline 3 hari lagi!"
                    1 -> "🔴 Tugas '$taskTitle' deadline BESOK!"
                    2 -> "🚨 Tugas '$taskTitle' harus dikumpulkan HARI INI!"
                    3 -> "❌ Tugas '$taskTitle' sudah LEWAT deadline!"
                    else -> "Ada update untuk tugas '$taskTitle'"
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Create intent to open app when notification is tapped
                val contentIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("taskId", taskId)
                    putExtra(NotificationScheduler.EXTRA_TASK_ID, taskId)
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    taskId.toInt(),
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val iconRes = R.drawable.ic_notification

                val notification = NotificationCompat.Builder(context, TaskKuApplication.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(iconRes)
                    .setContentTitle("Pengingat Tugas")
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify((taskId * 100 + type).toInt(), notification)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
