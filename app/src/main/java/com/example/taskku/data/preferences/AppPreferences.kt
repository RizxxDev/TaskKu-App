package com.example.taskku.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.taskku.domain.model.SortDirection
import com.example.taskku.domain.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

open class AppPreferences(private val prefs: SharedPreferences? = null) {
    constructor(context: Context) : this(context.getSharedPreferences("taskku_prefs", Context.MODE_PRIVATE))

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _sortOption = MutableStateFlow(loadSortOption())
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _sortDirection = MutableStateFlow(loadSortDirection())
    val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val name = prefs?.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try { ThemeMode.valueOf(name) } catch (e: Exception) { ThemeMode.SYSTEM }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs?.edit()?.putString("theme_mode", mode.name)?.apply()
        _themeMode.value = mode
    }

    private fun loadSortOption(): SortOption {
        val name = prefs?.getString("sort_option", SortOption.DEADLINE.name) ?: SortOption.DEADLINE.name
        return try { SortOption.valueOf(name) } catch (e: Exception) { SortOption.DEADLINE }
    }

    fun setSortOption(option: SortOption) {
        prefs?.edit()?.putString("sort_option", option.name)?.apply()
        _sortOption.value = option
    }

    private fun loadSortDirection(): SortDirection {
        val name = prefs?.getString("sort_direction", SortDirection.ASC.name) ?: SortDirection.ASC.name
        return try { SortDirection.valueOf(name) } catch (e: Exception) { SortDirection.ASC }
    }

    fun setSortDirection(direction: SortDirection) {
        prefs?.edit()?.putString("sort_direction", direction.name)?.apply()
        _sortDirection.value = direction
    }
}
