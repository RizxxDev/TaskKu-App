package com.example.taskku.ui.tasklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Color
import com.example.taskku.domain.model.TaskTag
import com.example.taskku.ui.components.EmptyState
import com.example.taskku.ui.components.SortOptionBar
import com.example.taskku.ui.components.SubjectFilterBar
import com.example.taskku.ui.components.TaskCard
import com.example.taskku.ui.components.getTagIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit,
    viewModel: TaskListViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSortOptions by remember { mutableStateOf(false) }
    var taskToDeleteId by remember { mutableStateOf<Long?>(null) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    // Single delete dialog
    if (taskToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { taskToDeleteId = null },
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah kamu yakin ingin menghapus tugas ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDeleteId?.let { viewModel.onDeleteTask(it) }
                        taskToDeleteId = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDeleteId = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Bulk delete dialog
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { Text("Hapus ${uiState.selectedTaskIds.size} Tugas") },
            text = { Text("Apakah kamu yakin ingin menghapus semua tugas yang dipilih?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onBulkDelete()
                        showBulkDeleteDialog = false
                    }
                ) {
                    Text("Hapus Semua", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                TopAppBar(
                    title = { Text("${uiState.selectedTaskIds.size} dipilih") },
                    navigationIcon = {
                        IconButton(onClick = viewModel::onClearSelection) {
                            Icon(Icons.Default.Close, contentDescription = "Batal Pilihan")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showBulkDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Terpilih", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Daftar Tugas") },
                    actions = {
                        IconButton(onClick = { showSortOptions = !showSortOptions }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Urutkan",
                                tint = if (showSortOptions) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = onAddTaskClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
        ) {
            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari tugas, deskripsi, mapel...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cari")
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Subject Filter Bar
            SubjectFilterBar(
                subjects = uiState.subjects,
                selectedSubjects = uiState.selectedSubjects,
                onSubjectToggle = viewModel::onSubjectToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Category / Tag Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedTag == null,
                        onClick = { viewModel.onTagSelect(null) },
                        label = { Text("Semua Tag") }
                    )
                }
                items(TaskTag.entries.toTypedArray()) { tag ->
                    val tagColor = remember(tag.colorHex) {
                        try { Color(android.graphics.Color.parseColor(tag.colorHex)) } catch (e: Exception) { Color(0xFF0984E3) }
                    }
                    val isSelected = uiState.selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onTagSelect(tag) },
                        label = { Text(tag.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = getTagIcon(tag),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tagColor.copy(alpha = 0.2f),
                            selectedLabelColor = tagColor,
                            selectedLeadingIconColor = tagColor
                        )
                    )
                }
            }

            // Animated Sort Option Bar
            AnimatedVisibility(visible = showSortOptions) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Urutkan Berdasarkan:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SortOptionBar(
                            currentSort = uiState.sortOption,
                            currentDirection = uiState.sortDirection,
                            onSortChange = viewModel::onSortChange,
                            onDirectionToggle = viewModel::onDirectionToggle
                        )
                    }
                }
            }

            // Task list or empty state
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.Assignment,
                        title = "Tidak Ada Tugas",
                        subtitle = if (uiState.searchQuery.isNotEmpty() || uiState.selectedSubjects.isNotEmpty())
                            "Tidak ada tugas yang sesuai dengan filter atau pencarianmu."
                        else
                            "Belum ada tugas. Tekan tombol + untuk menambahkan tugas baru!"
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.tasks.size} tugas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Urut: ${uiState.sortOption.label} (${if (uiState.sortDirection.name == "ASC") "Naik" else "Turun"})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val onDeleteClick = remember { { id: Long -> taskToDeleteId = id } }
                val onToggleSelection = remember(viewModel) { { id: Long -> viewModel.onToggleTaskSelection(id) } }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = if (uiState.isSelectionMode) 16.dp else 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { it.id },
                        contentType = { "task" }
                    ) { task ->
                        val isSelected = uiState.selectedTaskIds.contains(task.id)

                        if (uiState.isSelectionMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onToggleSelection(task.id) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    TaskCard(
                                        task = task,
                                        onTaskClick = onToggleSelection,
                                        onDeleteClick = onDeleteClick
                                    )
                                }
                            }
                        } else {
                            TaskCard(
                                task = task,
                                onTaskClick = onTaskClick,
                                onDeleteClick = onDeleteClick,
                                onLongClick = onToggleSelection
                            )
                        }
                    }
                }
            }
        }
    }
}
