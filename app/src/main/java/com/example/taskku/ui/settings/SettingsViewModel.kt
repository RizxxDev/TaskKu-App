package com.example.taskku.ui.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskku.data.preferences.AppPreferences
import com.example.taskku.data.preferences.ThemeMode
import com.example.taskku.data.repository.StatusRepository
import com.example.taskku.data.repository.SubjectRepository
import com.example.taskku.domain.model.Status
import com.example.taskku.domain.model.Subject
import com.example.taskku.export.ExportImportManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

@Immutable
sealed interface OperationResult {
    data object Idle : OperationResult
    data object InProgress : OperationResult
    data class Success(val message: String) : OperationResult
    data class Error(val message: String) : OperationResult
}

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val subjects: List<Subject> = emptyList(),
    val statuses: List<Status> = emptyList(),
    val operationResult: OperationResult = OperationResult.Idle,
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val appPreferences: AppPreferences,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val exportImportManager: ExportImportManager
) : ViewModel() {

    private val _operationResult = MutableStateFlow<OperationResult>(OperationResult.Idle)

    val uiState: StateFlow<SettingsUiState> = combine(
        appPreferences.themeMode,
        subjectRepository.getAllSubjects(),
        statusRepository.getAllStatuses(),
        _operationResult
    ) { theme, subjects, statuses, opResult ->
        SettingsUiState(
            themeMode = theme,
            subjects = subjects,
            statuses = statuses,
            operationResult = opResult,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onThemeChange(mode: ThemeMode) {
        appPreferences.setThemeMode(mode)
    }

    fun onAddSubject(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            subjectRepository.insertSubject(
                Subject(
                    name = name.trim(),
                    isPreset = false,
                    isVisible = true
                )
            )
        }
    }

    fun onDeleteSubject(subjectId: Long) {
        viewModelScope.launch {
            subjectRepository.deleteSubjectById(subjectId)
        }
    }

    fun onToggleSubjectVisibility(subjectId: Long, isVisible: Boolean) {
        viewModelScope.launch {
            subjectRepository.updateVisibility(subjectId, isVisible)
        }
    }

    fun onAddStatus(name: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val currentStatuses = uiState.value.statuses
            val nextSort = (currentStatuses.maxOfOrNull { it.sortOrder } ?: 0) + 1
            statusRepository.insertStatus(
                Status(
                    name = name.trim(),
                    colorHex = if (colorHex.startsWith("#")) colorHex else "#$colorHex",
                    sortOrder = nextSort,
                    isDefault = false
                )
            )
        }
    }

    fun onUpdateStatus(statusId: Long, name: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val current = uiState.value.statuses.find { it.id == statusId } ?: return@launch
            val color = if (colorHex.startsWith("#")) colorHex else "#$colorHex"
            statusRepository.updateStatus(current.copy(name = name.trim(), colorHex = color))
        }
    }

    fun onMoveStatusUp(status: Status) {
        val list = uiState.value.statuses
        val index = list.indexOfFirst { it.id == status.id }
        if (index > 0) {
            val prev = list[index - 1]
            viewModelScope.launch {
                val tempOrder = prev.sortOrder
                statusRepository.updateStatus(prev.copy(sortOrder = status.sortOrder))
                statusRepository.updateStatus(status.copy(sortOrder = tempOrder))
            }
        }
    }

    fun onMoveStatusDown(status: Status) {
        val list = uiState.value.statuses
        val index = list.indexOfFirst { it.id == status.id }
        if (index != -1 && index < list.lastIndex) {
            val next = list[index + 1]
            viewModelScope.launch {
                val tempOrder = next.sortOrder
                statusRepository.updateStatus(next.copy(sortOrder = status.sortOrder))
                statusRepository.updateStatus(status.copy(sortOrder = tempOrder))
            }
        }
    }

    fun onDeleteStatus(statusId: Long) {
        viewModelScope.launch {
            statusRepository.deleteStatusAndResetTasks(statusId)
        }
    }

    fun onExportData(outputStream: OutputStream) {
        viewModelScope.launch {
            _operationResult.value = OperationResult.InProgress
            val result = exportImportManager.exportData(outputStream)
            result.onSuccess { count ->
                _operationResult.value = OperationResult.Success("Berhasil mengekspor $count tugas!")
            }.onFailure { e ->
                _operationResult.value = OperationResult.Error("Gagal mengekspor: ${e.message ?: "Terjadi kesalahan"}")
            }
        }
    }

    fun onImportData(inputStream: InputStream) {
        viewModelScope.launch {
            _operationResult.value = OperationResult.InProgress
            val result = exportImportManager.importData(inputStream)
            result.onSuccess { count ->
                _operationResult.value = OperationResult.Success("Berhasil mengimpor $count tugas!")
            }.onFailure { e ->
                _operationResult.value = OperationResult.Error("Gagal mengimpor: ${e.message ?: "File tidak valid"}")
            }
        }
    }

    fun clearOperationResult() {
        _operationResult.value = OperationResult.Idle
    }
}

class SettingsViewModelFactory(
    private val appPreferences: AppPreferences,
    private val subjectRepository: SubjectRepository,
    private val statusRepository: StatusRepository,
    private val exportImportManager: ExportImportManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                appPreferences,
                subjectRepository,
                statusRepository,
                exportImportManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
