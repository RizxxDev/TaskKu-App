package com.example.taskku.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskku.domain.model.Task
import com.example.taskku.theme.*
import com.example.taskku.ui.components.DeadlineText
import com.example.taskku.ui.components.DifficultyBadge
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import com.example.taskku.domain.model.TimetableItem
import com.example.taskku.ui.util.isWideDisplay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.outlined.Assignment
import com.example.taskku.ui.timetable.calculateNextMeetingDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onAddHomeworkForSubject: ((String, Long) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val greeting = remember {
        val hour = java.time.LocalTime.now().hour
        when (hour) {
            in 4..10 -> "Selamat Pagi! 🌅"
            in 11..14 -> "Selamat Siang! ☀️"
            in 15..18 -> "Selamat Sore! 🌇"
            else -> "Selamat Malam! 🌙"
        }
    }

    val isWide = isWideDisplay()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.TaskAlt,
                                    contentDescription = "Logo TaskKu",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "TaskKu",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        },
        contentWindowInsets = if (isWide) ScaffoldDefaults.contentWindowInsets else ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (isWide) {
            // Balanced two-column layout for tablets, foldables, and landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Next class, Weekly progress, Quick stats
                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 24.dp, end = 4.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    NextClassCard(
                        nextClass = uiState.nextClassToday,
                        onAddTaskClick = onAddTaskClick,
                        onAddHomeworkForSubject = onAddHomeworkForSubject
                    )
                    WeeklyProgressCard(
                        doneCount = uiState.thisWeekDoneCount,
                        totalCount = uiState.thisWeekTotalCount,
                        progress = uiState.thisWeekProgress
                    )
                    QuickStatsSection(
                        pendingCount = uiState.pendingCount,
                        inProgressCount = uiState.inProgressCount,
                        doneCount = uiState.doneCount,
                        overdueCount = uiState.overdueCount
                    )
                }

                // Right Column: Urgent tasks list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(start = 4.dp, end = 24.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(key = "urgent_header", contentType = "urgent_header") {
                        UrgentTasksHeader(urgentCount = uiState.urgentTasks.size)
                    }

                    if (uiState.urgentTasks.isEmpty()) {
                        item(key = "urgent_empty", contentType = "urgent_empty") {
                            UrgentEmptyCard()
                        }
                    } else {
                        items(
                            items = uiState.urgentTasks,
                            key = { it.id },
                            contentType = { "urgent_task" }
                        ) { task ->
                            UrgentTaskCard(
                                task = task,
                                onTaskClick = onTaskClick,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        } else {
            // Single-column layout for compact portrait screens
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "next_class_card", contentType = "hero_card") {
                    NextClassCard(
                        nextClass = uiState.nextClassToday,
                        onAddTaskClick = onAddTaskClick,
                        onAddHomeworkForSubject = onAddHomeworkForSubject
                    )
                }

                item(key = "weekly_progress_card", contentType = "progress_card") {
                    WeeklyProgressCard(
                        doneCount = uiState.thisWeekDoneCount,
                        totalCount = uiState.thisWeekTotalCount,
                        progress = uiState.thisWeekProgress
                    )
                }

                item(key = "stat_cards_grid", contentType = "stat_cards") {
                    QuickStatsSection(
                        pendingCount = uiState.pendingCount,
                        inProgressCount = uiState.inProgressCount,
                        doneCount = uiState.doneCount,
                        overdueCount = uiState.overdueCount
                    )
                }

                item(key = "urgent_header", contentType = "urgent_header") {
                    UrgentTasksHeader(urgentCount = uiState.urgentTasks.size)
                }

                if (uiState.urgentTasks.isEmpty()) {
                    item(key = "urgent_empty", contentType = "urgent_empty") {
                        UrgentEmptyCard()
                    }
                } else {
                    items(
                        items = uiState.urgentTasks,
                        key = { it.id },
                        contentType = { "urgent_task" }
                    ) { task ->
                        UrgentTaskCard(
                            task = task,
                            onTaskClick = onTaskClick,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NextClassCard(
    nextClass: TimetableItem?,
    onAddTaskClick: () -> Unit,
    onAddHomeworkForSubject: ((String, Long) -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Jadwal Pelajaran Hari Ini",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (nextClass != null) {
                val isOngoing = nextClass.isCurrentlyActive()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isOngoing) "Sedang berlangsung saat ini:" else "Pelajaran berikutnya hari ini:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${nextClass.subject} (${nextClass.startTime} - ${nextClass.endTime})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isOngoing) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "Sedang Berlangsung",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (nextClass.room.isNotBlank() || nextClass.teacher.isNotBlank()) {
                        val details = listOfNotNull(
                            nextClass.room.takeIf { it.isNotBlank() }?.let { "Ruang $it" },
                            nextClass.teacher.takeIf { it.isNotBlank() }?.let { "Guru: $it" }
                        ).joinToString(" • ")
                        Text(
                            text = details,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            val nextMeeting = calculateNextMeetingDate(nextClass)
                            onAddHomeworkForSubject?.invoke(nextClass.subject, nextMeeting) ?: onAddTaskClick()
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ada PR untuk mapel ini?",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Text(
                    text = "Tidak ada jadwal pelajaran lagi hari ini 🎉",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgressCard(
    doneCount: Int,
    totalCount: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Progress Minggu Ini",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$doneCount dari $totalCount tugas selesai",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                val percent = remember(progress) { (progress * 100).toInt() }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    pendingCount: Int,
    inProgressCount: Int,
    doneCount: Int,
    overdueCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Statistik Cepat",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Belum Selesai",
                count = pendingCount,
                icon = Icons.Outlined.HourglassEmpty,
                color = StatusTodo,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Dikerjakan",
                count = inProgressCount,
                icon = Icons.Outlined.PendingActions,
                color = StatusInProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Selesai",
                count = doneCount,
                icon = Icons.Outlined.CheckCircle,
                color = StatusDone,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Overdue",
                count = overdueCount,
                icon = Icons.Outlined.ErrorOutline,
                color = CalendarOverdue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun UrgentTasksHeader(urgentCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⚠️ Tugas Mendesak",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (urgentCount > 0) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text("$urgentCount")
                }
            }
        }
    }
}

@Composable
private fun UrgentEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.ThumbUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Tidak ada tugas mendesak dalam 3 hari ke depan!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun UrgentTaskCard(
    task: Task,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onTaskClick(task.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                DifficultyBadge(difficulty = task.difficulty)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.subject,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                DeadlineText(deadlineMillis = task.deadlineMillis)
            }
        }
    }
}
