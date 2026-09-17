package com.example.taskku.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.automirrored.outlined.MenuBook
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
import com.example.taskku.data.preferences.ThemeMode
import com.example.taskku.domain.model.Status
import com.example.taskku.domain.model.Subject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var statusToEdit by remember { mutableStateOf<Status?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    var statusToDelete by remember { mutableStateOf<Status?>(null) }

    // Export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            val outputStream = context.contentResolver.openOutputStream(it)
            if (outputStream != null) {
                viewModel.onExportData(outputStream)
            }
        }
    }

    // Import launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            if (inputStream != null) {
                viewModel.onImportData(inputStream)
            }
        }
    }

    // React to OperationResult
    LaunchedEffect(uiState.operationResult) {
        when (val result = uiState.operationResult) {
            is OperationResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.clearOperationResult()
            }
            is OperationResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.clearOperationResult()
            }
            else -> {}
        }
    }

    // Dialogs
    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name ->
                viewModel.onAddSubject(name)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddStatusDialog) {
        AddStatusDialog(
            onDismiss = { showAddStatusDialog = false },
            onConfirm = { name, colorHex ->
                viewModel.onAddStatus(name, colorHex)
                showAddStatusDialog = false
            }
        )
    }

    if (statusToEdit != null) {
        EditStatusDialog(
            status = statusToEdit!!,
            onDismiss = { statusToEdit = null },
            onConfirm = { name, colorHex ->
                statusToEdit?.let { viewModel.onUpdateStatus(it.id, name, colorHex) }
                statusToEdit = null
            }
        )
    }

    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Hapus Mata Pelajaran") },
            text = { Text("Apakah kamu yakin ingin menghapus mata pelajaran \"${subjectToDelete?.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        subjectToDelete?.let { viewModel.onDeleteSubject(it.id) }
                        subjectToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (statusToDelete != null) {
        AlertDialog(
            onDismissRequest = { statusToDelete = null },
            title = { Text("Hapus Status") },
            text = {
                Text(
                    "Apakah kamu yakin ingin menghapus status \"${statusToDelete?.name}\"?\n" +
                            "Tugas yang memiliki status ini akan otomatis direset menjadi \"Belum Dikerjakan\"."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        statusToDelete?.let { viewModel.onDeleteStatus(it.id) }
                        statusToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { statusToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Tema Aplikasi
            item {
                SectionCard(title = "Tema Tampilan", icon = Icons.Outlined.Palette) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Pilih tema warna yang nyaman untuk matamu:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                label = "Sistem",
                                selected = uiState.themeMode == ThemeMode.SYSTEM,
                                onClick = { viewModel.onThemeChange(ThemeMode.SYSTEM) },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                label = "Terang",
                                selected = uiState.themeMode == ThemeMode.LIGHT,
                                onClick = { viewModel.onThemeChange(ThemeMode.LIGHT) },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                label = "Gelap",
                                selected = uiState.themeMode == ThemeMode.DARK,
                                onClick = { viewModel.onThemeChange(ThemeMode.DARK) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Kelola Mata Pelajaran
            item {
                SectionCard(
                    title = "Kelola Mata Pelajaran",
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    action = {
                        IconButton(onClick = { showAddSubjectDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah Mapel", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Aktifkan atau nonaktifkan mata pelajaran di daftar filter:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        uiState.subjects.forEach { subject ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    if (subject.isPreset) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "Bawaan",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = subject.isVisible,
                                        onCheckedChange = { viewModel.onToggleSubjectVisibility(subject.id, it) }
                                    )
                                    if (!subject.isPreset) {
                                        IconButton(onClick = { subjectToDelete = subject }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Kelola Status Tugas
            item {
                SectionCard(
                    title = "Kelola Status Tugas",
                    icon = Icons.Outlined.Checklist,
                    action = {
                        IconButton(onClick = { showAddStatusDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah Status", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Status bawaan tidak dapat dihapus. Status kustom yang dihapus akan mereset tugas ke \"Belum Dikerjakan\".",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        uiState.statuses.forEachIndexed { sIdx, status ->
                            val statusColor = remember(status.colorHex) {
                                try {
                                    Color(android.graphics.Color.parseColor(status.colorHex))
                                } catch (e: Exception) {
                                    Color.Gray
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = status.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    if (status.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "Default",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.onMoveStatusUp(status) },
                                        enabled = sIdx > 0,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.KeyboardArrowUp,
                                            contentDescription = "Pindah Ke Atas",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.onMoveStatusDown(status) },
                                        enabled = sIdx < uiState.statuses.lastIndex,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Pindah Ke Bawah",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    if (!status.isDefault) {
                                        IconButton(
                                            onClick = { statusToEdit = status },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = "Edit Status",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { statusToDelete = status },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus Status",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Cadangkan & Pulihkan (Backup & Restore)
            item {
                SectionCard(title = "Cadangkan & Pulihkan", icon = Icons.Outlined.CloudSync) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Ekspor semua data tugas dan file lampiran ke file .zip, atau pulihkan dari file backup.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (uiState.operationResult is OperationResult.InProgress) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sedang memproses data...", style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
                                        exportLauncher.launch("TaskKu_Backup_$timeStamp.zip")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ekspor Data")
                                }

                                OutlinedButton(
                                    onClick = {
                                        importLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed", "*/*"))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Impor Data")
                                }
                            }
                        }
                    }
                }
            }

            // 5. Tentang Aplikasi
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.TaskAlt,
                                    contentDescription = "Logo TaskKu",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "TaskKu",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Versi 1.0.0",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pencatat & Pengingat Tugas Sekolah",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                action?.invoke()
            }
            content()
        }
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Mata Pelajaran") },
        text = {
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Nama Mata Pelajaran") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subjectName.isNotBlank()) {
                        onConfirm(subjectName.trim())
                    }
                },
                enabled = subjectName.isNotBlank()
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

private data class PresetColor(val hex: String, val color: Color)

private val PRESET_COLOR_OPTIONS = listOf(
    PresetColor("#6C5CE7", Color(0xFF6C5CE7)),
    PresetColor("#00B894", Color(0xFF00B894)),
    PresetColor("#FDCB6E", Color(0xFFFDCB6E)),
    PresetColor("#E17055", Color(0xFFE17055)),
    PresetColor("#74B9FF", Color(0xFF74B9FF)),
    PresetColor("#A29BFE", Color(0xFFA29BFE)),
    PresetColor("#FD79A8", Color(0xFFFD79A8)),
    PresetColor("#00CEC9", Color(0xFF00CEC9))
)

@Composable
private fun AddStatusDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var statusName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(PRESET_COLOR_OPTIONS[0].hex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Status Kustom") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = statusName,
                    onValueChange = { statusName = it },
                    label = { Text("Nama Status") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pilih Warna:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PRESET_COLOR_OPTIONS.forEach { opt ->
                            val isSelected = opt.hex == selectedColor
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                                    .then(
                                        if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { selectedColor = opt.hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (statusName.isNotBlank()) {
                        onConfirm(statusName.trim(), selectedColor)
                    }
                },
                enabled = statusName.isNotBlank()
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

@Composable
private fun EditStatusDialog(
    status: Status,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var statusName by remember { mutableStateOf(status.name) }
    var selectedColor by remember { mutableStateOf(status.colorHex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Status") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = statusName,
                    onValueChange = { statusName = it },
                    label = { Text("Nama Status") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pilih Warna:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PRESET_COLOR_OPTIONS.forEach { opt ->
                            val isSelected = opt.hex.equals(selectedColor, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                                    .then(
                                        if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { selectedColor = opt.hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (statusName.isNotBlank()) {
                        onConfirm(statusName.trim(), selectedColor)
                    }
                },
                enabled = statusName.isNotBlank()
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
