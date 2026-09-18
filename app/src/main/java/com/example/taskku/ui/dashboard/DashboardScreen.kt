package com.example.taskku.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskku.domain.model.Task
import com.example.taskku.theme.*
import com.example.taskku.ui.components.DeadlineText
import com.example.taskku.ui.components.DifficultyBadge
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
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..10 -> "Selamat Pagi! 🌅"
            in 11..14 -> "Selamat Siang! ☀️"
            in 15..18 -> "Selamat Sore! 🌇"
            else -> "Selamat Malam! 🌙"
        }
    }

    // 1. Header & welcome greeting slide down + fade in on enter (0ms delay)
    var isHeaderVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isHeaderVisible = true
    }

    val headerAlpha by animateFloatAsState(
        targetValue = if (isHeaderVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "headerAlpha"
    )
    val headerTranslationY by animateFloatAsState(
        targetValue = if (isHeaderVisible) 0f else -20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "headerTranslationY"
    )

    // Staggered entrance for content activates once database loading completes
    var isContentVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isContentVisible = true
        }
    }

    // Hero cards (Next Class & Weekly Progress)
    val heroAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 30, easing = FastOutSlowInEasing),
        label = "heroAlpha"
    )
    val heroTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 30, easing = FastOutSlowInEasing),
        label = "heroTranslationY"
    )

    // Stat cards staggered slide up + fade in (30ms increments)
    val statPendingAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 50, easing = FastOutSlowInEasing),
        label = "statPendingAlpha"
    )
    val statPendingTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 50, easing = FastOutSlowInEasing),
        label = "statPendingTranslationY"
    )

    val statInProgressAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 80, easing = FastOutSlowInEasing),
        label = "statInProgressAlpha"
    )
    val statInProgressTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 80, easing = FastOutSlowInEasing),
        label = "statInProgressTranslationY"
    )

    val statDoneAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 110, easing = FastOutSlowInEasing),
        label = "statDoneAlpha"
    )
    val statDoneTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 110, easing = FastOutSlowInEasing),
        label = "statDoneTranslationY"
    )

    val statOverdueAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "statOverdueAlpha"
    )
    val statOverdueTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "statOverdueTranslationY"
    )

    // Urgent tasks section (header + empty card / items) smoothly fades in last
    val urgentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 170, easing = FastOutSlowInEasing),
        label = "urgentAlpha"
    )
    val urgentTranslationY by animateFloatAsState(
        targetValue = if (isContentVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 250, delayMillis = 170, easing = FastOutSlowInEasing),
        label = "urgentTranslationY"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.graphicsLayer {
                            alpha = headerAlpha
                            translationY = headerTranslationY
                        }
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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Pelajaran Berikutnya Hari Ini Card
                item {
                    val nextClass = uiState.nextClassToday
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = heroAlpha
                                translationY = heroTranslationY
                            },
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
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
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
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
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

                // 1. Progress Minggu Ini Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = heroAlpha
                                translationY = heroTranslationY
                            },
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
                                Column {
                                    Text(
                                        text = "Progress Minggu Ini",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "${uiState.thisWeekDoneCount} dari ${uiState.thisWeekTotalCount} tugas selesai",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                                val percent = remember(uiState.thisWeekProgress) { (uiState.thisWeekProgress * 100).toInt() }
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { uiState.thisWeekProgress },
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

                // 2. Statistik Cepat Section (Staggered 80ms delay increments)
                item {
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
                                count = uiState.pendingCount,
                                icon = Icons.Outlined.HourglassEmpty,
                                color = StatusTodo,
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        alpha = statPendingAlpha
                                        translationY = statPendingTranslationY
                                    }
                            )
                            StatCard(
                                title = "Dikerjakan",
                                count = uiState.inProgressCount,
                                icon = Icons.Outlined.PendingActions,
                                color = StatusInProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        alpha = statInProgressAlpha
                                        translationY = statInProgressTranslationY
                                    }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "Selesai",
                                count = uiState.doneCount,
                                icon = Icons.Outlined.CheckCircle,
                                color = StatusDone,
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        alpha = statDoneAlpha
                                        translationY = statDoneTranslationY
                                    }
                            )
                            StatCard(
                                title = "Overdue",
                                count = uiState.overdueCount,
                                icon = Icons.Outlined.ErrorOutline,
                                color = CalendarOverdue,
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        alpha = statOverdueAlpha
                                        translationY = statOverdueTranslationY
                                    }
                            )
                        }
                    }
                }

                // 3. Tugas Mendesak Section Header & Empty state (fades in last)
                item {
                    Column(
                        modifier = Modifier.graphicsLayer {
                            alpha = urgentAlpha
                            translationY = urgentTranslationY
                        },
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
                                if (uiState.urgentTasks.isNotEmpty()) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("${uiState.urgentTasks.size}")
                                    }
                                }
                            }
                        }

                        if (uiState.urgentTasks.isEmpty()) {
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
                    }
                }

                // 3b. Tugas Mendesak Items with recycling, stable keys, and smooth animation
                if (uiState.urgentTasks.isNotEmpty()) {
                    items(
                        items = uiState.urgentTasks,
                        key = { it.id },
                        contentType = { "urgent_task" }
                    ) { task ->
                        UrgentTaskCard(
                            task = task,
                            onTaskClick = onTaskClick,
                            modifier = Modifier
                                .animateItem()
                                .graphicsLayer {
                                    alpha = urgentAlpha
                                    translationY = urgentTranslationY
                                }
                        )
                    }
                }
            }
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "urgentTaskPressScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple()
            ) { onTaskClick(task.id) },
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
                    modifier = Modifier.weight(1f)
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
                    color = MaterialTheme.colorScheme.primary
                )
                DeadlineText(deadlineMillis = task.deadlineMillis)
            }
        }
    }
}
