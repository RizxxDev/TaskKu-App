package com.example.taskku.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskku.ui.timetable.TimetableScreen
import com.example.taskku.ui.timetable.TimetableViewModel
import com.example.taskku.ui.timetable.TimetableViewModelFactory
import com.example.taskku.ui.util.isCompactHeight
import com.example.taskku.ui.util.isWideDisplay

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

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { NavigationTab.entries.size }
    )

    val isWide = isWideDisplay()

    val onTaskDetailClick = remember(onNavigate) { { taskId: Long -> onNavigate(TaskDetail(taskId)) } }
    val onAddTaskClick = remember(onNavigate) { { onNavigate(TaskForm(null)) } }
    val onAddHomeworkForSubject = remember(onNavigate) {
        { subject: String, deadlineDate: Long ->
            onNavigate(TaskForm(taskId = null, initialSubject = subject, initialDeadlineDate = deadlineDate, initialTag = "PR"))
        }
    }

    @Composable
    fun TabContent() {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 0,
            userScrollEnabled = false,
            key = { page -> NavigationTab.entries[page].name },
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (NavigationTab.entries[tabIndex]) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        onTaskClick = onTaskDetailClick,
                        onAddTaskClick = onAddTaskClick,
                        onAddHomeworkForSubject = onAddHomeworkForSubject,
                        viewModel = dashboardViewModel
                    )
                }
                NavigationTab.TASKS -> {
                    TaskListScreen(
                        onTaskClick = onTaskDetailClick,
                        onAddTaskClick = onAddTaskClick,
                        viewModel = taskListViewModel
                    )
                }
                NavigationTab.TIMETABLE -> {
                    TimetableScreen(
                        onNavigateToTaskForm = onAddHomeworkForSubject,
                        viewModel = timetableViewModel
                    )
                }
                NavigationTab.CALENDAR -> {
                    CalendarScreen(
                        onTaskClick = onTaskDetailClick,
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

    val isCompactHeight = isCompactHeight()

    if (isWide) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = modifier
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    header = {
                        val headerSize = if (isCompactHeight) 32.dp else 42.dp
                        val iconSize = if (isCompactHeight) 18.dp else 24.dp
                        val headerPadding = if (isCompactHeight) 6.dp else 12.dp
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .padding(top = headerPadding, bottom = headerPadding)
                                .size(headerSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.TaskAlt,
                                    contentDescription = "Logo TaskKu",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavigationTab.entries.forEachIndexed { index, tab ->
                            val isSelected = pagerState.currentPage == index

                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    if (pagerState.currentPage != index) {
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(index)
                                        }
                                    }
                                },
                                icon = {
                                    AnimatedNavIcon(
                                        isSelected = isSelected,
                                        selectedIcon = tab.selectedIcon,
                                        unselectedIcon = tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                alwaysShowLabel = !isCompactHeight
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clipToBounds()
                ) {
                    TabContent()
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationTab.entries.forEachIndexed { index, tab ->
                        val isSelected = pagerState.currentPage == index

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (pagerState.currentPage != index) {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                }
                            },
                            icon = {
                                AnimatedNavIcon(
                                    isSelected = isSelected,
                                    selectedIcon = tab.selectedIcon,
                                    unselectedIcon = tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
                TabContent()
            }
        }
    }
}

@Composable
private fun AnimatedNavIcon(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String?
) {
    val iconScale = remember { Animatable(1.0f) }

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

    Icon(
        imageVector = if (isSelected) selectedIcon else unselectedIcon,
        contentDescription = contentDescription,
        modifier = Modifier.graphicsLayer {
            scaleX = iconScale.value
            scaleY = iconScale.value
        }
    )
}

@Composable
internal fun MainScreen(data: List<String>, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier) {
        data.forEach { Text(text = "Hello $it!") }
    }
}
