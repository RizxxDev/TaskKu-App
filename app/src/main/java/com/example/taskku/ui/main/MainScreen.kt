package com.example.taskku.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
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

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    val iconScale = remember { Animatable(1.0f) }

                    LaunchedEffect(isSelected) {
                        if (isSelected) {
                            iconScale.animateTo(
                                targetValue = 1.15f,
                                animationSpec = tween(120, easing = FastOutSlowInEasing)
                            )
                            iconScale.animateTo(
                                targetValue = 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        } else {
                            iconScale.snapTo(1.0f)
                        }
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
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
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally(
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                targetOffsetX = { fullWidth -> -fullWidth }
                            ) + fadeOut(animationSpec = tween(300))
                        )
                    } else {
                        (slideInHorizontally(
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                targetOffsetX = { fullWidth -> fullWidth }
                            ) + fadeOut(animationSpec = tween(300))
                        )
                    }
                },
                label = "TabContentAnimation",
                modifier = Modifier.fillMaxSize()
            ) { tabIndex ->
                when (NavigationTab.entries[tabIndex]) {
                    NavigationTab.DASHBOARD -> {
                        val dashboardViewModel: DashboardViewModel = viewModel(
                            factory = DashboardViewModelFactory(container.taskRepository, container.timetableRepository)
                        )
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
                        val taskListViewModel: TaskListViewModel = viewModel(
                            factory = TaskListViewModelFactory(
                                taskRepository = container.taskRepository,
                                subjectRepository = container.subjectRepository,
                                statusRepository = container.statusRepository,
                                appPreferences = container.appPreferences,
                                appContext = context.applicationContext
                            )
                        )
                        TaskListScreen(
                            onTaskClick = { taskId -> onNavigate(TaskDetail(taskId)) },
                            onAddTaskClick = { onNavigate(TaskForm(null)) },
                            viewModel = taskListViewModel
                        )
                    }
                    NavigationTab.TIMETABLE -> {
                        val timetableViewModel: TimetableViewModel = viewModel(
                            factory = TimetableViewModelFactory(
                                timetableRepository = container.timetableRepository,
                                subjectRepository = container.subjectRepository
                            )
                        )
                        TimetableScreen(
                            onNavigateToTaskForm = { subject, deadlineDate ->
                                onNavigate(TaskForm(taskId = null, initialSubject = subject, initialDeadlineDate = deadlineDate, initialTag = "PR"))
                            },
                            viewModel = timetableViewModel
                        )
                    }
                    NavigationTab.CALENDAR -> {
                        val calendarViewModel: CalendarViewModel = viewModel(
                            factory = CalendarViewModelFactory(container.taskRepository)
                        )
                        CalendarScreen(
                            onTaskClick = { taskId -> onNavigate(TaskDetail(taskId)) },
                            viewModel = calendarViewModel
                        )
                    }
                    NavigationTab.SETTINGS -> {
                        val settingsViewModel: SettingsViewModel = viewModel(
                            factory = SettingsViewModelFactory(
                                appPreferences = container.appPreferences,
                                subjectRepository = container.subjectRepository,
                                statusRepository = container.statusRepository,
                                exportImportManager = container.exportImportManager
                            )
                        )
                        SettingsScreen(
                            viewModel = settingsViewModel
                        )
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
