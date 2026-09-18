package com.example.taskku.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.example.taskku.MainActivity
import com.example.taskku.TaskKuApplication
import com.example.taskku.domain.model.Task
import com.example.taskku.notification.NotificationScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.taskku.domain.model.TimetableItem

val TaskIdKey = ActionParameters.Key<Long>("task_id")
val TargetCompletedKey = ActionParameters.Key<Boolean>("target_completed")

class TaskKuWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as? TaskKuApplication
        val taskRepository = app?.container?.taskRepository
        val timetableRepository = app?.container?.timetableRepository

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 2)
        val endOfTomorrow = cal.timeInMillis - 1

        val allTasks = try {
            taskRepository?.getAllTasks()?.first() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val nextClass = try {
            timetableRepository?.getUpcomingNextClass()?.firstOrNull()
        } catch (e: Exception) {
            null
        }

        // Filter for "Hari Ini & Besok":
        // include tasks where deadlineDate is within startOfToday..endOfTomorrow, or overdue pending tasks
        val filteredTasks = allTasks.filter { task ->
            (task.deadlineDate in startOfToday..endOfTomorrow) || (!task.isCompleted && task.deadlineDate < startOfToday)
        }.sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenBy { it.deadlineDate }
        )

        val pendingCount = filteredTasks.count { !it.isCompleted }

        provideContent {
            GlanceTheme {
                WidgetRoot(
                    context = context,
                    tasks = filteredTasks,
                    pendingCount = pendingCount,
                    startOfToday = startOfToday,
                    nextClass = nextClass
                )
            }
        }
    }
}

@Composable
private fun WidgetRoot(
    context: Context,
    tasks: List<Task>,
    pendingCount: Int,
    startOfToday: Long,
    nextClass: TimetableItem? = null
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Tugas Hari Ini & Besok",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = if (pendingCount > 0) "$pendingCount tugas tersisa" else "Semua tugas selesai 🎉",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = 12.sp
                    )
                )
            }

            Text(
                text = "Buka App ↗",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }

        if (nextClass != null) {
            val roomText = if (nextClass.room.isNotBlank()) " • Ruang ${nextClass.room}" else ""
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 Pelajaran berikutnya: ${nextClass.subject} (${nextClass.startTime} - ${nextClass.endTime})$roomText",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }

        // Content
        if (tasks.isEmpty()) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tidak ada tugas hari ini & besok! 🎉",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Ketuk untuk menambah tugas baru",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                items(tasks) { task ->
                    TaskItemRow(
                        context = context,
                        task = task,
                        startOfToday = startOfToday
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemRow(
    context: Context,
    task: Task,
    startOfToday: Long
) {
    val deadlineLabel = formatWidgetDeadline(task.deadlineDate, task.deadlineTime, startOfToday)
    val taskIdActionParam = ActionParameters.Key<Long>("taskId")

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(10.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckBox(
            checked = task.isCompleted,
            onCheckedChange = actionRunCallback<ToggleTaskActionCallback>(
                actionParametersOf(
                    TaskIdKey to task.id,
                    TargetCompletedKey to !task.isCompleted
                )
            )
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        Column(
            modifier = GlanceModifier
                .defaultWeight()
                .clickable(actionStartActivity<MainActivity>(actionParametersOf(taskIdActionParam to task.id)))
        ) {
            Text(
                text = task.title,
                style = TextStyle(
                    color = if (task.isCompleted) GlanceTheme.colors.outline else GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                maxLines = 1
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = "${task.subject} • $deadlineLabel",
                style = TextStyle(
                    color = if (task.isCompleted) GlanceTheme.colors.outline else GlanceTheme.colors.primary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

private fun formatWidgetDeadline(deadlineDate: Long, deadlineTime: String, startOfToday: Long): String {
    val oneDayMillis = 24 * 60 * 60 * 1000L
    val startOfTomorrow = startOfToday + oneDayMillis
    val startOfDayAfterTomorrow = startOfTomorrow + oneDayMillis

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

class ToggleTaskActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val taskId = parameters[TaskIdKey] ?: return
        val targetCompleted = parameters[TargetCompletedKey] ?: true

        val app = context.applicationContext as? TaskKuApplication ?: return
        val taskRepository = app.container.taskRepository
        val statusRepository = app.container.statusRepository

        try {
            val statuses = statusRepository.getAllStatuses().first()
            val targetStatus = if (targetCompleted) {
                statuses.find { it.isCompleted }
                    ?: statuses.lastOrNull()
            } else {
                statuses.find { it.isDefault && !it.isCompleted }
                    ?: statuses.firstOrNull()
            }

            if (targetStatus != null) {
                taskRepository.updateTaskStatus(taskId, targetStatus.id)
                if (targetCompleted) {
                    NotificationScheduler.cancelNotifications(context, taskId)
                } else {
                    val task = taskRepository.getTaskById(taskId).firstOrNull()
                    if (task != null) {
                        val updatedTask = task.copy(
                            statusId = targetStatus.id,
                            statusName = targetStatus.name,
                            statusColorHex = targetStatus.colorHex
                        )
                        NotificationScheduler.updateTaskNotifications(context, updatedTask)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        TaskKuWidget().update(context, glanceId)
    }
}
