package com.example.taskku.ui.taskform

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.ReminderOffset
import com.example.taskku.domain.model.TaskTag
import com.example.taskku.domain.model.TaskType
import com.example.taskku.ui.components.getTagIcon
import com.example.taskku.util.FileStorageHelper
import java.text.SimpleDateFormat
import java.util.*

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    taskId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TaskFormViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newMemberName by remember { mutableStateOf("") }
    var newSubtaskTitle by remember { mutableStateOf("") }
    var selectedMemberIndexForSubtask by remember { mutableStateOf<Int?>(null) }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    var memberDropdownExpanded by remember { mutableStateOf(false) }

    // Notification Permission Launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    // File Picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.processSelectedUris(uris, context) { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.deadlineDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            // Preserve time of day
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = selectedMillis
                                val parts = formState.deadlineTime.split(":")
                                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 23
                                val min = parts.getOrNull(1)?.toIntOrNull() ?: 59
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, min)
                                set(Calendar.SECOND, 0)
                            }
                            viewModel.updateDeadlineDate(cal.timeInMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Tambah Mata Pelajaran Baru") },
            text = {
                OutlinedTextField(
                    value = newSubjectName,
                    onValueChange = { newSubjectName = it },
                    label = { Text("Nama Mapel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubjectName.isNotBlank()) {
                            viewModel.addNewSubject(newSubjectName)
                            newSubjectName = ""
                            showAddSubjectDialog = false
                        }
                    }
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (formState.isEditMode) "Edit Tugas" else "Tambah Tugas Baru") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveTask(onSuccess = onSaved)
                        },
                        enabled = !formState.isSaving
                    ) {
                        if (formState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Simpan", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (formState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 720.dp)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Error banner if any
                formState.errors["general"]?.let { errorMsg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // 1. Judul Tugas
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Judul Tugas *") },
                    isError = formState.errors.containsKey("title"),
                    supportingText = formState.errors["title"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // 2. Mata Pelajaran (Dropdown + Custom)
                ExposedDropdownMenuBox(
                    expanded = subjectDropdownExpanded,
                    onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = formState.subject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mata Pelajaran *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                        isError = formState.errors.containsKey("subject"),
                        supportingText = formState.errors["subject"]?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectDropdownExpanded,
                        onDismissRequest = { subjectDropdownExpanded = false }
                    ) {
                        formState.availableSubjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub.name) },
                                onClick = {
                                    viewModel.updateSubject(sub.name)
                                    subjectDropdownExpanded = false
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("+ Tambah Mapel Baru", color = MaterialTheme.colorScheme.primary)
                                }
                            },
                            onClick = {
                                subjectDropdownExpanded = false
                                showAddSubjectDialog = true
                            }
                        )
                    }
                }

                // 3. Tipe Tugas (Pribadi / Kelompok)
                Column {
                    Text("Tipe Tugas", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = formState.type == TaskType.PRIBADI,
                            onClick = { viewModel.updateType(TaskType.PRIBADI) },
                            label = { Text("Pribadi") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = formState.type == TaskType.KELOMPOK,
                            onClick = { viewModel.updateType(TaskType.KELOMPOK) },
                            label = { Text("Kelompok") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Group, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Jenis Tagihan Tugas (PR, Kuis, Praktikum, Proyek)
                Column {
                    Text("Jenis Tagihan Tugas *", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskTag.entries.take(2).forEach { tag ->
                            val tagColor = remember(tag.colorHex) {
                                try { Color(android.graphics.Color.parseColor(tag.colorHex)) } catch (e: Exception) { Color(0xFF0984E3) }
                            }
                            FilterChip(
                                selected = formState.tag == tag,
                                onClick = { viewModel.updateTag(tag) },
                                label = { Text(tag.displayName) },
                                leadingIcon = {
                                    Icon(getTagIcon(tag), contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tagColor.copy(alpha = 0.2f),
                                    selectedLabelColor = tagColor,
                                    selectedLeadingIconColor = tagColor
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskTag.entries.drop(2).forEach { tag ->
                            val tagColor = remember(tag.colorHex) {
                                try { Color(android.graphics.Color.parseColor(tag.colorHex)) } catch (e: Exception) { Color(0xFF0984E3) }
                            }
                            FilterChip(
                                selected = formState.tag == tag,
                                onClick = { viewModel.updateTag(tag) },
                                label = { Text(tag.displayName) },
                                leadingIcon = {
                                    Icon(getTagIcon(tag), contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tagColor.copy(alpha = 0.2f),
                                    selectedLabelColor = tagColor,
                                    selectedLeadingIconColor = tagColor
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 4. Deskripsi / Instruksi Tugas
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("Deskripsi / Instruksi (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )

                // 5. Tanggal & Waktu Deadline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
                    val dateFormatted = remember(formState.deadlineDate) {
                        dateFormatter.format(Date(formState.deadlineDate))
                    }

                    OutlinedTextField(
                        value = dateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tanggal Deadline *") },
                        trailingIcon = {
                            Icon(
                                Icons.Outlined.CalendarToday,
                                contentDescription = "Pilih Tanggal",
                                modifier = Modifier.clickable { showDatePicker = true }
                            )
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = formState.deadlineTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jam") },
                        trailingIcon = {
                            Icon(
                                Icons.Outlined.AccessTime,
                                contentDescription = "Pilih Jam",
                                modifier = Modifier.clickable {
                                    val parts = formState.deadlineTime.split(":")
                                    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 23
                                    val min = parts.getOrNull(1)?.toIntOrNull() ?: 59
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            val formatted = String.format(Locale.ROOT, "%02d:%02d", h, m)
                                            viewModel.updateDeadlineTime(formatted)
                                        },
                                        hour,
                                        min,
                                        true
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 120.dp)
                            .clickable {
                                val parts = formState.deadlineTime.split(":")
                                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 23
                                val min = parts.getOrNull(1)?.toIntOrNull() ?: 59
                                TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        val formatted = String.format(Locale.ROOT, "%02d:%02d", h, m)
                                        viewModel.updateDeadlineTime(formatted)
                                    },
                                    hour,
                                    min,
                                    true
                                ).show()
                            },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 6. Tingkat Kesulitan
                Column {
                    Text("Tingkat Kesulitan *", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Difficulty.entries.forEach { diff ->
                            FilterChip(
                                selected = formState.difficulty == diff,
                                onClick = { viewModel.updateDifficulty(diff) },
                                label = { Text(diff.displayName) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 7. Status Tugas
                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
                ) {
                    val currentStatus = formState.availableStatuses.find { it.id == formState.statusId }
                    OutlinedTextField(
                        value = currentStatus?.name ?: "Belum Dikerjakan",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status Tugas *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        formState.availableStatuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.name) },
                                onClick = {
                                    viewModel.updateStatusId(st.id)
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 8. Notifikasi Pengingat Switch & Opsi Fleksibel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Notifikasi Pengingat", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Pengingat sebelum batas waktu deadline",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = formState.notificationEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                    viewModel.updateNotificationEnabled(enabled)
                                }
                            )
                        }

                        AnimatedVisibility(visible = formState.notificationEnabled) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    "Pilihan Waktu Pengingat:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ReminderOffset.entries.forEach { offset ->
                                        FilterChip(
                                            selected = formState.reminderOffset == offset,
                                            onClick = { viewModel.updateReminderOffset(offset) },
                                            label = { Text(offset.displayName) },
                                            leadingIcon = if (formState.reminderOffset == offset) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            } else null
                                        )
                                    }
                                }

                                val hintText = when (formState.reminderOffset) {
                                    ReminderOffset.ONE_DAY_19_00 -> "Pengingat akan dikirim H-1 sebelum deadline pada jam 19.00 malam."
                                    ReminderOffset.THREE_HOURS_BEFORE -> "Pengingat akan dikirim 3 jam sebelum batas waktu deadline."
                                    ReminderOffset.ONE_HOUR_BEFORE -> "Pengingat akan dikirim 1 jam sebelum batas waktu deadline."
                                    ReminderOffset.ON_DEADLINE -> "Pengingat akan dikirim tepat saat batas waktu deadline."
                                }
                                Text(
                                    text = hintText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 9. Khusus Tugas Kelompok Section
                AnimatedVisibility(visible = formState.type == TaskType.KELOMPOK) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Detail Tugas Kelompok",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Nama Kelompok
                            OutlinedTextField(
                                value = formState.groupName,
                                onValueChange = viewModel::updateGroupName,
                                label = { Text("Nama Kelompok (opsional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Anggota Kelompok
                            Text("Anggota Kelompok", style = MaterialTheme.typography.labelMedium)
                            if (formState.members.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    formState.members.forEachIndexed { idx, memberName ->
                                        InputChip(
                                            selected = true,
                                            onClick = {},
                                            label = { Text(memberName) },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Hapus",
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { viewModel.removeMember(idx) }
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newMemberName,
                                    onValueChange = { newMemberName = it },
                                    label = { Text("Nama Anggota") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newMemberName.isNotBlank()) {
                                            viewModel.addMember(newMemberName)
                                            newMemberName = ""
                                        }
                                    }
                                ) {
                                    Text("Tambah")
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Sub-Tugas List
                            Text("Daftar Sub-Tugas", style = MaterialTheme.typography.labelMedium)
                            if (formState.subtasks.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    formState.subtasks.forEachIndexed { idx, subtask ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
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
                                                    Text(subtask.title, style = MaterialTheme.typography.bodyMedium)
                                                    val assignedName = subtask.assignedMemberIndex?.let {
                                                        formState.members.getOrNull(it)
                                                    }
                                                    if (assignedName != null) {
                                                        Text(
                                                            "Assigned: $assignedName",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                                IconButton(onClick = { viewModel.removeSubtask(idx) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Hapus Subtugas", tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Add Subtask form
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newSubtaskTitle,
                                    onValueChange = { newSubtaskTitle = it },
                                    label = { Text("Judul Sub-Tugas") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (formState.members.isNotEmpty()) {
                                    ExposedDropdownMenuBox(
                                        expanded = memberDropdownExpanded,
                                        onExpandedChange = { memberDropdownExpanded = !memberDropdownExpanded }
                                    ) {
                                        val assignedName = selectedMemberIndexForSubtask?.let {
                                            formState.members.getOrNull(it)
                                        } ?: "Pilih Anggota (opsional)"

                                        OutlinedTextField(
                                            value = assignedName,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Assign ke Anggota") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded) },
                                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = memberDropdownExpanded,
                                            onDismissRequest = { memberDropdownExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Tidak di-assign") },
                                                onClick = {
                                                    selectedMemberIndexForSubtask = null
                                                    memberDropdownExpanded = false
                                                }
                                            )
                                            formState.members.forEachIndexed { mIdx, mName ->
                                                DropdownMenuItem(
                                                    text = { Text(mName) },
                                                    onClick = {
                                                        selectedMemberIndexForSubtask = mIdx
                                                        memberDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (newSubtaskTitle.isNotBlank()) {
                                            viewModel.addSubtask(newSubtaskTitle, selectedMemberIndexForSubtask)
                                            newSubtaskTitle = ""
                                            selectedMemberIndexForSubtask = null
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tambah Sub-Tugas")
                                }
                            }
                        }
                    }
                }

                // 10. Lampiran File (PRD 2.5)
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Lampiran File", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Foto (10MB), Video (50MB), Dokumen (25MB)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(onClick = { filePickerLauncher.launch("*/*") }) {
                                Icon(Icons.Outlined.AttachFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pilih File")
                            }
                        }

                        if (formState.attachments.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                formState.attachments.forEachIndexed { aIdx, attachment ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = when (attachment.fileType) {
                                                        "photo" -> Icons.Outlined.Image
                                                        "video" -> Icons.Outlined.Videocam
                                                        else -> Icons.Outlined.Description
                                                    },
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        attachment.fileName,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        FileStorageHelper.formatFileSize(attachment.fileSize),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            IconButton(onClick = { viewModel.removeAttachment(aIdx) }) {
                                                Icon(Icons.Default.Close, contentDescription = "Hapus File", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 11. Simpan Button
                Button(
                    onClick = { viewModel.saveTask(onSuccess = onSaved) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !formState.isSaving
                ) {
                    if (formState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(if (formState.isEditMode) "Perbarui Tugas" else "Simpan Tugas", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
}
