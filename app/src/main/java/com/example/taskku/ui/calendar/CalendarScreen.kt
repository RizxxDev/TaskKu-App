package com.example.taskku.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskku.domain.model.Task
import com.example.taskku.theme.CalendarDone
import com.example.taskku.theme.CalendarOverdue
import com.example.taskku.theme.CalendarUpcoming
import com.example.taskku.theme.CalendarUrgent
import com.example.taskku.ui.components.EmptyState
import com.example.taskku.ui.components.TaskCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val DAYS_OF_WEEK = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onTaskClick: (Long) -> Unit,
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("id", "ID")) }
    val selectedDateFormat = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalender Tugas", fontWeight = FontWeight.Bold) }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Month Navigation Header
                item(key = "month_card", contentType = "header") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                var totalDrag = 0f
                                detectHorizontalDragGestures(
                                    onDragStart = { totalDrag = 0f },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDrag += dragAmount
                                    },
                                    onDragEnd = {
                                        if (totalDrag > 60f) {
                                            viewModel.onPreviousMonth()
                                        } else if (totalDrag < -60f) {
                                            viewModel.onNextMonth()
                                        }
                                    }
                                )
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = viewModel::onPreviousMonth) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Bulan Sebelumnya"
                                    )
                                }
                                Text(
                                    text = monthFormat.format(uiState.currentMonth.time),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = viewModel::onNextMonth) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Bulan Berikutnya"
                                    )
                                }
                            }

                            // Day of Week Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                DAYS_OF_WEEK.forEach { dayName ->
                                    Text(
                                        text = dayName,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Calendar Days Grid
                            CalendarGrid(
                                currentMonth = uiState.currentMonth,
                                selectedDate = uiState.selectedDate,
                                tasksByDate = uiState.tasksByDate,
                                onDateSelected = viewModel::onDateSelected
                            )

                            // Status legend
                            StatusLegend()
                        }
                    }
                }

                // 2. Selected Date Header
                item(key = "selected_date_header", contentType = "header") {
                    Text(
                        text = selectedDateFormat.format(uiState.selectedDate.time),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 3. Task list for selected date
                if (uiState.selectedDateTasks.isEmpty()) {
                    item(key = "empty_state", contentType = "empty") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyState(
                                    icon = Icons.Outlined.EventBusy,
                                    title = "Tidak Ada Tugas",
                                    subtitle = "Tidak ada batas waktu tugas pada tanggal ini."
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.selectedDateTasks,
                        key = { it.id },
                        contentType = { "task" }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onTaskClick = onTaskClick
                        )
                    }
                }
            }
        }
    }
}

@Immutable
private data class CalendarCellData(
    val dayNumber: Int,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dotColors: List<Color>,
    val dateMillis: Long
)

@Immutable
private data class CalendarGridInfo(
    val totalRows: Int,
    val firstDayOfWeek: Int,
    val daysInMonth: Int,
    val cells: Map<Int, CalendarCellData>
)

@Composable
private fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: Calendar,
    tasksByDate: Map<String, List<Task>>,
    onDateSelected: (Long) -> Unit
) {
    val gridInfo = remember(currentMonth, selectedDate, tasksByDate) {
        val cal = currentMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val todayCal = Calendar.getInstance()
        val isSameMonthAsToday = todayCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                todayCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
        val todayDay = if (isSameMonthAsToday) todayCal.get(Calendar.DAY_OF_MONTH) else -1

        val isSameMonthAsSelected = selectedDate.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
        val selectedDay = if (isSameMonthAsSelected) selectedDate.get(Calendar.DAY_OF_MONTH) else -1

        val totalCells = firstDayOfWeek + daysInMonth
        val totalRows = (totalCells + 6) / 7

        val now = System.currentTimeMillis()
        val year = currentMonth.get(Calendar.YEAR)
        val month = currentMonth.get(Calendar.MONTH) + 1
        val cellMap = HashMap<Int, CalendarCellData>(daysInMonth)

        for (day in 1..daysInMonth) {
            val dateKey = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day)
            val tasks = tasksByDate[dateKey] ?: emptyList()

            val dotColors = ArrayList<Color>(4)
            tasks.forEach { task ->
                val color = when {
                    task.isCompleted -> CalendarDone
                    task.deadlineDate < now -> CalendarOverdue
                    (task.deadlineDate - now) <= 3 * 24 * 60 * 60 * 1000L -> CalendarUrgent
                    else -> CalendarUpcoming
                }
                if (!dotColors.contains(color)) {
                    dotColors.add(color)
                }
            }

            val dayCal = (currentMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }

            cellMap[day] = CalendarCellData(
                dayNumber = day,
                isToday = day == todayDay,
                isSelected = day == selectedDay,
                dotColors = dotColors,
                dateMillis = dayCal.timeInMillis
            )
        }

        CalendarGridInfo(totalRows, firstDayOfWeek, daysInMonth, cellMap)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until gridInfo.totalRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - gridInfo.firstDayOfWeek + 1

                    val cellData = gridInfo.cells[dayNumber]
                    if (cellData != null) {
                        DayCell(
                            dayNumber = cellData.dayNumber,
                            isToday = cellData.isToday,
                            isSelected = cellData.isSelected,
                            dotColors = cellData.dotColors,
                            onClick = { onDateSelected(cellData.dateMillis) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    isToday: Boolean,
    isSelected: Boolean,
    dotColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                when {
                    isSelected -> Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    isToday -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                    else -> Modifier
                }
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$dayNumber",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            ),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Dot indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dotColors.take(4).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun StatusLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = CalendarOverdue, label = "Terlambat")
        LegendItem(color = CalendarUrgent, label = "Mendesak")
        LegendItem(color = CalendarUpcoming, label = "Akan Datang")
        LegendItem(color = CalendarDone, label = "Selesai")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
