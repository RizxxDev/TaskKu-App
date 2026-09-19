package com.example.taskku.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import com.example.taskku.TaskDetail
import com.example.taskku.TaskForm
import com.example.taskku.TaskKuApplication
import com.example.taskku.ui.calendar.CalendarScreen
import com.example.taskku.ui.calendar.CalendarViewModel
import com.example.taskku.ui.calendar.CalendarViewModelFactory
import com.example.taskku.ui.dashboard.DashboardScreen
import com.example.taskku.ui.dashboard.DashboardViewModel
import com.example.taskku.ui.dashboard.DashboardViewModelFactory
import com.example.taskku.ui.settings.SettingsScreen
import com.example.taskku.ui.settings.SettingsViewModel
import com.example.taskku.ui.settings.SettingsViewModelFactory
import com.example.taskku.ui.tasklist.TaskListScreen
import com.example.taskku.ui.tasklist.TaskListViewModel
import com.example.taskku.ui.tasklist.TaskListViewModelFactory

import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Schedule
import com.example.taskku.ui.timetable.TimetableScreen
import com.example.taskku.ui.timetable.TimetableViewModel
import com.example.taskku.ui.timetable.TimetableViewModelFactory

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    TASKS("Tugas", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment),
    TIMETABLE("Jadwal", Icons.Filled.Schedule, Icons.Outlined.Schedule),
    CALENDAR("Kalender", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    SETTINGS("Pengaturan", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainScreen(
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskKuApplication
    val container = app.container

    val dashboardViewModelFactory = remember(container) {
        DashboardViewModelFactory(container.taskRepository, container.timetableRepository)
    }
    val appContext = remember(context) { context.applicationContext }
    val taskListViewModelFactory = remember(container, appContext) {
        TaskListViewModelFactory(
            taskRepository = container.taskRepository,
            subjectRepository = container.subjectRepository,
            statusRepository = container.statusRepository,
            appPreferences = container.appPreferences,
            appContext = appContext
        )
    }
    val timetableViewModelFactory = remember(container) {
        TimetableViewModelFactory(
            timetableRepository = container.timetableRepository,
            subjectRepository = container.subjectRepository
        )
    }
    val calendarViewModelFactory = remember(container) {
        CalendarViewModelFactory(container.taskRepository)
    }
    val settingsViewModelFactory = remember(container) {
        SettingsViewModelFactory(
            appPreferences = container.appPreferences,
            subjectRepository = container.subjectRepository,
            statusRepository = container.statusRepository,
            exportImportManager = container.exportImportManager
        )
    }

    val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory)
    val taskListViewModel: TaskListViewModel = viewModel(factory = taskListViewModelFactory)
    val timetableViewModel: TimetableViewModel = viewModel(factory = timetableViewModelFactory)
    val calendarViewModel: CalendarViewModel = viewModel(factory = calendarViewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val saveableStateHolder = rememberSaveableStateHolder()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    val iconScale = remember(tab) { Animatable(1.0f) }

                    LaunchedEffect(isSelected) {
                        if (isSelected) {
                            iconScale.animateTo(
                                targetValue = 1.12f,
                                animationSpec = tween(90, easing = FastOutSlowInEasing)
                            )
                            iconScale.animateTo(
                                targetValue = 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        } else {
                            iconScale.snapTo(1.0f)
                        }
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (selectedTabIndex == index) {
                                coroutineScope.launch {
                                    iconScale.snapTo(1.0f)
                                    iconScale.animateTo(
                                        targetValue = 1.12f,
                                        animationSpec = tween(90, easing = FastOutSlowInEasing)
                                    )
                                    iconScale.animateTo(
                                        targetValue = 1.0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            } else {
                                selectedTabIndex = index
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = iconScale.value
                                    scaleY = iconScale.value
                                }
                            )
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .consumeWindowInsets(PaddingValues(bottom = innerPadding.calculateBottomPadding()))
                .clipToBounds()
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "TabContentAnimation",
                modifier = Modifier.fillMaxSize()
            ) { tabIndex ->
                saveableStateHolder.SaveableStateProvider(key = tabIndex) {
                    when (NavigationTab.entries[tabIndex]) {
                        NavigationTab.DASHBOARD -> {
                            DashboardScreen(
                                onTaskClick = { taskId -> onNavigate(TaskDetail(taskId)) },
                                onAddTaskClick = { onNavigate(TaskForm(null)) },
                                onAddHomeworkForSubject = { subject, deadlineDate ->
                                    onNavigate(TaskForm(taskId = null, initialSubject = subject, initialDeadlineDate = deadlineDate, initialTag = "PR"))
                                },
                                viewModel = dashboardViewModel
                            )
                        }
                        NavigationTab.TASKS -> {
                            TaskListScreen(
                                onTaskClick = { taskId -> onNavigate(TaskDetail(taskId)) },
                                onAddTaskClick = { onNavigate(TaskForm(null)) },
                                viewModel = taskListViewModel
                            )
                        }
                        NavigationTab.TIMETABLE -> {
                            TimetableScreen(
                                onNavigateToTaskForm = { subject, deadlineDate ->
                                    onNavigate(TaskForm(taskId = null, initialSubject = subject, initialDeadlineDate = deadlineDate, initialTag = "PR"))
                                },
                                viewModel = timetableViewModel
                            )
                        }
                        NavigationTab.CALENDAR -> {
                            CalendarScreen(
                                onTaskClick = { taskId -> onNavigate(TaskDetail(taskId)) },
                                viewModel = calendarViewModel
                            )
                        }
                        NavigationTab.SETTINGS -> {
                            SettingsScreen(
                                viewModel = settingsViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MainScreen(data: List<String>, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier) {
        data.forEach { Text(text = "Hello $it!") }
    }
}
