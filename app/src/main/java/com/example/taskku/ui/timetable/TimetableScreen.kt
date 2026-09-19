package com.example.taskku.ui.timetable

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import com.example.taskku.ui.util.isWideDisplay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskku.domain.model.SchoolDay
import com.example.taskku.domain.model.TimetableItem
import java.util.Locale

private val SCHOOL_DAYS = listOf(
    SchoolDay.SENIN,
    SchoolDay.SELASA,
    SchoolDay.RABU,
    SchoolDay.KAMIS,
    SchoolDay.JUMAT,
    SchoolDay.SABTU
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    onNavigateToTaskForm: (subject: String, deadlineDate: Long) -> Unit,
    viewModel: TimetableViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<TimetableItem?>(null) }
    var itemToDelete by remember { mutableStateOf<TimetableItem?>(null) }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Jadwal") },
            text = { Text("Apakah kamu yakin ingin menghapus jadwal ${item.subject} (${item.dayName}, ${item.timeRange})?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTimetable(item.id)
                        itemToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        TimetableAddEditDialog(
            initialItem = editingItem,
            defaultDay = uiState.selectedDay,
            availableSubjects = uiState.availableSubjects.map { it.name },
            onDismiss = {
                showAddEditDialog = false
                editingItem = null
            },
            onSave = { id, subject, dayOfWeek, startTime, endTime, room, teacher ->
                viewModel.saveTimetable(
                    id = id,
                    subject = subject,
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                    room = room,
                    teacher = teacher
                )
                viewModel.onSelectDay(dayOfWeek)
                showAddEditDialog = false
                editingItem = null
            }
        )
    }

    val isWide = isWideDisplay()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "Jadwal Pelajaran",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        },
        contentWindowInsets = if (isWide) ScaffoldDefaults.contentWindowInsets else ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            @Composable
            fun DayTabs() {
                SCHOOL_DAYS.forEach { schoolDay ->
                    val isSelected = uiState.selectedDay == schoolDay.dayOfWeek
                    val count = uiState.timetablesByDay[schoolDay.dayOfWeek]?.size ?: 0
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.onSelectDay(schoolDay.dayOfWeek) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(schoolDay.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (count > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.sizeIn(minWidth = 18.dp, minHeight = 18.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            val selectedDayIndex = remember(uiState.selectedDay) {
                SCHOOL_DAYS.indexOfFirst { it.dayOfWeek == uiState.selectedDay }.coerceAtLeast(0)
            }

            // Day selection tab row: TabRow on wide displays, ScrollableTabRow on compact displays
            if (isWide) {
                TabRow(
                    selectedTabIndex = selectedDayIndex,
                    divider = { HorizontalDivider() }
                ) {
                    DayTabs()
                }
            } else {
                ScrollableTabRow(
                    selectedTabIndex = selectedDayIndex,
                    edgePadding = 16.dp,
                    divider = { HorizontalDivider() }
                ) {
                    DayTabs()
                }
            }

            // Timetable items list for selected day
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.currentDayTimetables.isEmpty()) {
                val dayName = SchoolDay.fromDayOfWeek(uiState.selectedDay).displayName
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.EventBusy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Text(
                            text = "Belum Ada Jadwal di Hari $dayName",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tambahkan mata pelajaran untuk menyusun jadwal mingguanmu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FilledTonalButton(
                            onClick = {
                                editingItem = null
                                showAddEditDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah Jadwal $dayName")
                        }
                    }
                }
            } else if (isWide) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 340.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.currentDayTimetables,
                        key = { it.id },
                        contentType = { "timetable_slot" }
                    ) { item ->
                        TimetableSlotCard(
                            item = item,
                            onEdit = {
                                editingItem = item
                                showAddEditDialog = true
                            },
                            onDelete = {
                                itemToDelete = item
                            },
                            onAddHomework = {
                                val nextMeeting = calculateNextMeetingDate(item)
                                onNavigateToTaskForm(item.subject, nextMeeting)
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.currentDayTimetables,
                        key = { it.id },
                        contentType = { "timetable_slot" }
                    ) { item ->
                        TimetableSlotCard(
                            item = item,
                            onEdit = {
                                editingItem = item
                                showAddEditDialog = true
                            },
                            onDelete = {
                                itemToDelete = item
                            },
                            onAddHomework = {
                                val nextMeeting = calculateNextMeetingDate(item)
                                onNavigateToTaskForm(item.subject, nextMeeting)
                            }
                        )
                    }
                }
            }
        }
    }
}

private val TimetableCardShape = RoundedCornerShape(16.dp)
private val OngoingBadgeShape = RoundedCornerShape(4.dp)
private val AddHomeworkButtonShape = RoundedCornerShape(10.dp)
private val TimetableCardElevation = 2.dp

@Composable
fun TimetableSlotCard(
    item: TimetableItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddHomework: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isActive = remember(item) { item.isCurrentlyActive() }
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val containerColor = remember(isActive, primaryContainer, surfaceVariant) {
        if (isActive) primaryContainer.copy(alpha = 0.4f) else surfaceVariant
    }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val dividerColor = remember(outlineVariant) {
        outlineVariant.copy(alpha = 0.5f)
    }
    val onOpenMenu = remember { { menuExpanded = true } }
    val onDismissMenu = remember { { menuExpanded = false } }
    val onEditClick = remember(onEdit) { { menuExpanded = false; onEdit() } }
    val onDeleteClick = remember(onDelete) { { menuExpanded = false; onDelete() } }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = TimetableCardShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = TimetableCardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Subject and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.subject,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isActive) {
                            Surface(
                                shape = OngoingBadgeShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
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

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = item.timeRange,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opsi")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = onDismissMenu
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Jadwal") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = onEditClick
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = onDeleteClick
                        )
                    }
                }
            }

            // Row 2: Room & Teacher info if available
            if (item.room.isNotBlank() || item.teacher.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.room.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MeetingRoom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = item.room,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (item.teacher.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = item.teacher,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = dividerColor)

            // Row 3: Shortcut action button "Ada PR untuk mapel ini?"
            FilledTonalButton(
                onClick = onAddHomework,
                modifier = Modifier.fillMaxWidth(),
                shape = AddHomeworkButtonShape,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ada PR untuk mapel ini?",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableAddEditDialog(
    initialItem: TimetableItem?,
    defaultDay: Int,
    availableSubjects: List<String>,
    onDismiss: () -> Unit,
    onSave: (id: Long, subject: String, dayOfWeek: Int, startTime: String, endTime: String, room: String, teacher: String) -> Unit
) {
    val context = LocalContext.current
    var subject by remember { mutableStateOf(initialItem?.subject ?: availableSubjects.firstOrNull() ?: "Matematika") }
    var dayOfWeek by remember { mutableIntStateOf(initialItem?.dayOfWeek ?: defaultDay) }
    var startTime by remember { mutableStateOf(initialItem?.startTime ?: "07:30") }
    var endTime by remember { mutableStateOf(initialItem?.endTime ?: "09:00") }
    var room by remember { mutableStateOf(initialItem?.room ?: "") }
    var teacher by remember { mutableStateOf(initialItem?.teacher ?: "") }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var dayDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem != null) "Edit Jadwal Pelajaran" else "Tambah Jadwal Pelajaran") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                errorMessage?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Subject dropdown / input
                ExposedDropdownMenuBox(
                    expanded = subjectDropdownExpanded,
                    onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Mata Pelajaran *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectDropdownExpanded,
                        onDismissRequest = { subjectDropdownExpanded = false }
                    ) {
                        availableSubjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub) },
                                onClick = {
                                    subject = sub
                                    subjectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Day dropdown
                ExposedDropdownMenuBox(
                    expanded = dayDropdownExpanded,
                    onExpandedChange = { dayDropdownExpanded = !dayDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = SchoolDay.fromDayOfWeek(dayOfWeek).displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hari *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = dayDropdownExpanded,
                        onDismissRequest = { dayDropdownExpanded = false }
                    ) {
                        listOf(
                            SchoolDay.SENIN,
                            SchoolDay.SELASA,
                            SchoolDay.RABU,
                            SchoolDay.KAMIS,
                            SchoolDay.JUMAT,
                            SchoolDay.SABTU
                        ).forEach { sd ->
                            DropdownMenuItem(
                                text = { Text(sd.displayName) },
                                onClick = {
                                    dayOfWeek = sd.dayOfWeek
                                    dayDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Time picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mulai *") },
                            trailingIcon = {
                                Icon(
                                    Icons.Outlined.AccessTime,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    val parts = startTime.split(":")
                                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 7
                                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 30
                                    TimePickerDialog(context, { _, hour, min ->
                                        startTime = String.format(Locale.ROOT, "%02d:%02d", hour, min)
                                    }, h, m, true).show()
                                }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selesai *") },
                            trailingIcon = {
                                Icon(
                                    Icons.Outlined.AccessTime,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    val parts = endTime.split(":")
                                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 9
                                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                    TimePickerDialog(context, { _, hour, min ->
                                        endTime = String.format(Locale.ROOT, "%02d:%02d", hour, min)
                                    }, h, m, true).show()
                                }
                        )
                    }
                }

                // Room input
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Ruangan (opsional, misal: Lab IPA / 10A)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Teacher input
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Guru / Dosen (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.trim().isEmpty()) {
                        errorMessage = "Mata pelajaran tidak boleh kosong"
                        return@Button
                    }
                    if (startTime >= endTime) {
                        errorMessage = "Jam selesai harus setelah jam mulai"
                        return@Button
                    }
                    onSave(initialItem?.id ?: 0L, subject, dayOfWeek, startTime, endTime, room, teacher)
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
