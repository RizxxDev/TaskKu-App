package com.example.taskku.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import com.example.taskku.ui.util.isWideDisplay
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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

    val isWide = isWideDisplay()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalender Tugas", fontWeight = FontWeight.Bold) }
            )
        },
        contentWindowInsets = if (isWide) ScaffoldDefaults.contentWindowInsets else ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
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
        } else if (isWide) {
            // Side-by-side two-pane layout for wide displays (tablets/foldables/landscape)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left pane: Monthly calendar grid & status legend
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 24.dp, end = 4.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MonthCalendarCard(
                        currentYearMonth = uiState.currentYearMonth,
                        formattedMonth = uiState.formattedMonth,
                        selectedDate = uiState.selectedDate,
                        tasksByDate = uiState.tasksByDate,
                        onPreviousMonth = viewModel::onPreviousMonth,
                        onNextMonth = viewModel::onNextMonth,
                        onDateSelected = viewModel::onDateSelected
                    )
                }

                // Right pane: Selected date tasks list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(start = 4.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "selected_date_header", contentType = "header") {
                        Text(
                            text = uiState.formattedSelectedDate,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (uiState.selectedDateTasks.isEmpty()) {
                        item(key = "empty_state", contentType = "empty") {
                            CalendarEmptyCard()
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
        } else {
            // Single-column layout for compact portrait screens
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "month_card", contentType = "header") {
                    MonthCalendarCard(
                        currentYearMonth = uiState.currentYearMonth,
                        formattedMonth = uiState.formattedMonth,
                        selectedDate = uiState.selectedDate,
                        tasksByDate = uiState.tasksByDate,
                        onPreviousMonth = viewModel::onPreviousMonth,
                        onNextMonth = viewModel::onNextMonth,
                        onDateSelected = viewModel::onDateSelected
                    )
                }

                item(key = "selected_date_header", contentType = "header") {
                    Text(
                        text = uiState.formattedSelectedDate,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (uiState.selectedDateTasks.isEmpty()) {
                    item(key = "empty_state", contentType = "empty") {
                        CalendarEmptyCard()
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

@Composable
private fun MonthCalendarCard(
    currentYearMonth: YearMonth,
    formattedMonth: String,
    selectedDate: LocalDate,
    tasksByDate: Map<String, List<Task>>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                            onPreviousMonth()
                        } else if (totalDrag < -60f) {
                            onNextMonth()
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
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Bulan Sebelumnya"
                    )
                }
                Text(
                    text = formattedMonth,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onNextMonth) {
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
                currentYearMonth = currentYearMonth,
                selectedDate = selectedDate,
                tasksByDate = tasksByDate,
                onDateSelected = onDateSelected
            )

            // Status legend
            StatusLegend()
        }
    }
}

private val DayCellShape = RoundedCornerShape(10.dp)
private val CalendarEmptyCardShape = RoundedCornerShape(16.dp)

@Composable
private fun CalendarEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CalendarEmptyCardShape,
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

@Immutable
private data class CalendarCellData(
    val dayNumber: Int,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dotColors: List<Color>,
    val date: LocalDate
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
    currentYearMonth: YearMonth,
    selectedDate: LocalDate,
    tasksByDate: Map<String, List<Task>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val gridInfo = remember(currentYearMonth, selectedDate, tasksByDate) {
        val firstDayOfMonth = currentYearMonth.atDay(1)
        val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value + 6) % 7
        val daysInMonth = currentYearMonth.lengthOfMonth()

        val today = LocalDate.now()
        val isSameMonthAsToday = currentYearMonth == YearMonth.from(today)
        val todayDay = if (isSameMonthAsToday) today.dayOfMonth else -1

        val isSameMonthAsSelected = currentYearMonth == YearMonth.from(selectedDate)
        val selectedDay = if (isSameMonthAsSelected) selectedDate.dayOfMonth else -1

        val totalCells = firstDayOfWeek + daysInMonth
        val totalRows = (totalCells + 6) / 7

        val now = System.currentTimeMillis()
        val cellMap = HashMap<Int, CalendarCellData>(daysInMonth)

        for (day in 1..daysInMonth) {
            val dayDate = currentYearMonth.atDay(day)
            val dateKey = dayDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
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

            cellMap[day] = CalendarCellData(
                dayNumber = day,
                isToday = day == todayDay,
                isSelected = day == selectedDay,
                dotColors = dotColors,
                date = dayDate
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
                            onClick = { onDateSelected(cellData.date) },
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
            .defaultMinSize(minWidth = 32.dp, minHeight = 36.dp)
            .padding(2.dp)
            .clip(DayCellShape)
            .then(
                when {
                    isSelected -> Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    isToday -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, DayCellShape)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusLegend() {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
