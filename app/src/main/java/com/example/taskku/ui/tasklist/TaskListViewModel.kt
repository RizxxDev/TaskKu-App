package com.example.taskku.ui.tasklist

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.preferences.AppPreferences
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.data.repository.TaskRepository
import com.example.taskku.domain.model.Difficulty
import com.example.taskku.domain.model.SortDirection
import com.example.taskku.domain.model.SortOption
import com.example.taskku.domain.model.Task
import com.example.taskku.notification.NotificationScheduler
import com.example.taskku.ui.components.SubjectWithCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val appPreferences: AppPreferences? = null,
    private val appContext: Context? = null
) : ViewModel() {

    @Immutable
    data class UiState(
        val tasks: List<Task> = emptyList(),
        val subjects: List<SubjectWithCount> = emptyList(),
        val selectedSubjects: Set<String> = emptySet(),
        val sortOption: SortOption = SortOption.DEADLINE,
        val sortDirection: SortDirection = SortDirection.ASC,
        val searchQuery: String = "",
        val selectedTaskIds: Set<Long> = emptySet(),
        val isSelectionMode: Boolean = false,
        val isLoading: Boolean = false
    )

    private val _selectedSubjects = MutableStateFlow<Set<String>>(emptySet())
    private val _sortOption = MutableStateFlow(appPreferences?.sortOption?.value ?: SortOption.DEADLINE)
    private val _sortDirection = MutableStateFlow(appPreferences?.sortDirection?.value ?: SortDirection.ASC)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTaskIds = MutableStateFlow<Set<Long>>(emptySet())

    private data class FilterParams(
        val selectedSubjects: Set<String>,
        val sortOption: SortOption,
        val sortDirection: SortDirection,
        val searchQuery: String,
        val selectedTaskIds: Set<Long>
    )

    private val filterParamsFlow: Flow<FilterParams> = combine(
        _selectedSubjects,
        _sortOption,
        _sortDirection,
        _searchQuery,
        _selectedTaskIds
    ) { selectedSubs, sortOpt, sortDir, query, selectedIds ->
        FilterParams(selectedSubs, sortOpt, sortDir, query, selectedIds)
    }

    val uiState: StateFlow<UiState> = combine(
        taskRepository.getAllTasks(),
        subjectRepository.getVisibleSubjects(),
        filterParamsFlow
    ) { rawTasks, visibleSubjects, filters ->
        // Calculate pending task counts per subject
        val pendingCounts = rawTasks.filter { !it.isCompleted }
            .groupingBy { it.subject }
            .eachCount()

        val subjectWithCounts = visibleSubjects.map { sub ->
            SubjectWithCount(
                name = sub.name,
                count = pendingCounts[sub.name] ?: 0
            )
        }.filter { it.count > 0 || filters.selectedSubjects.contains(it.name) }

        // Filter tasks
        var filtered = rawTasks
        if (filters.selectedSubjects.isNotEmpty() && !filters.selectedSubjects.contains("Semua")) {
            filtered = filtered.filter { it.subject in filters.selectedSubjects }
        }

        if (filters.searchQuery.isNotBlank()) {
            val q = filters.searchQuery.trim().lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.subject.lowercase().contains(q) ||
                it.groupName.lowercase().contains(q)
            }
        }

        // Sort tasks
        val sorted = when (filters.sortOption) {
            SortOption.DEADLINE -> {
                if (filters.sortDirection == SortDirection.ASC) filtered.sortedBy { it.deadlineDate }
                else filtered.sortedByDescending { it.deadlineDate }
            }
            SortOption.DIFFICULTY -> {
                val weight = { d: Difficulty ->
                    when (d) {
                        Difficulty.MUDAH -> 1
                        Difficulty.SEDANG -> 2
                        Difficulty.SULIT -> 3
                    }
                }
                if (filters.sortDirection == SortDirection.ASC) filtered.sortedBy { weight(it.difficulty) }
                else filtered.sortedByDescending { weight(it.difficulty) }
            }
            SortOption.CREATED_DATE -> {
                if (filters.sortDirection == SortDirection.ASC) filtered.sortedBy { it.createdAt }
                else filtered.sortedByDescending { it.createdAt }
            }
            SortOption.SUBJECT -> {
                if (filters.sortDirection == SortDirection.ASC) filtered.sortedBy { it.subject.lowercase() }
                else filtered.sortedByDescending { it.subject.lowercase() }
            }
            else -> filtered
        }

        UiState(
            tasks = sorted,
            subjects = subjectWithCounts,
            selectedSubjects = filters.selectedSubjects,
            sortOption = filters.sortOption,
            sortDirection = filters.sortDirection,
            searchQuery = filters.searchQuery,
            selectedTaskIds = filters.selectedTaskIds,
            isSelectionMode = filters.selectedTaskIds.isNotEmpty(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = true)
    )

    fun onSubjectToggle(subjectName: String) {
        if (subjectName == "Semua") {
            _selectedSubjects.value = emptySet()
            return
        }
        val current = _selectedSubjects.value.toMutableSet()
        if (current.contains(subjectName)) {
            current.remove(subjectName)
        } else {
            current.add(subjectName)
        }
        _selectedSubjects.value = current
    }

    fun onSortChange(option: SortOption) {
        _sortOption.value = option
        _sortDirection.value = option.defaultDirection
        appPreferences?.setSortOption(option)
        appPreferences?.setSortDirection(option.defaultDirection)
    }

    fun onDirectionToggle() {
        val newDir = if (_sortDirection.value == SortDirection.ASC) SortDirection.DESC else SortDirection.ASC
        _sortDirection.value = newDir
        appPreferences?.setSortDirection(newDir)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onToggleTaskSelection(taskId: Long) {
        val current = _selectedTaskIds.value.toMutableSet()
        if (current.contains(taskId)) {
            current.remove(taskId)
        } else {
            current.add(taskId)
        }
        _selectedTaskIds.value = current
    }

    fun onClearSelection() {
        _selectedTaskIds.value = emptySet()
    }

    fun onDeleteTask(taskId: Long) {
        viewModelScope.launch {
            appContext?.let { NotificationScheduler.cancelNotifications(it, taskId) }
            taskRepository.deleteTaskById(taskId)
        }
    }

    fun onBulkDelete() {
        val idsToDelete = _selectedTaskIds.value.toList()
        viewModelScope.launch {
            idsToDelete.forEach { id ->
                appContext?.let { NotificationScheduler.cancelNotifications(it, id) }
            }
            taskRepository.deleteTasksByIds(idsToDelete)
            _selectedTaskIds.value = emptySet()
        }
    }
}

class TaskListViewModelFactory(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val appPreferences: AppPreferences? = null,
    private val appContext: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(taskRepository, subjectRepository, statusRepository, appPreferences, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
