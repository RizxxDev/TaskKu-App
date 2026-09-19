package com.example.taskku

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.taskku.ui.main.MainScreen
import com.example.taskku.ui.taskdetail.TaskDetailScreen
import com.example.taskku.ui.taskdetail.TaskDetailViewModel
import com.example.taskku.ui.taskdetail.TaskDetailViewModelFactory
import com.example.taskku.ui.taskform.TaskFormScreen
import com.example.taskku.ui.taskform.TaskFormViewModel
import com.example.taskku.ui.taskform.TaskFormViewModelFactory
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MainNavigation(
    initialTaskId: Long? = null,
    onTaskIdHandled: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(Main)

    val handleBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(initialTaskId) {
        if (initialTaskId != null && initialTaskId > 0) {
            backStack.add(TaskDetail(initialTaskId))
            onTaskIdHandled?.invoke()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = handleBack,
        transitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 180, easing = LinearOutSlowInEasing)
            )).togetherWith(
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 150)
                )
            )
        },
        popTransitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 200)
            )).togetherWith(
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 150)
                )
            )
        },
        entryProvider = entryProvider {
            entry<Main> {
                MainScreen(
                    onNavigate = { navKey -> backStack.add(navKey) }
                )
            }
            entry<TaskDetail> { key ->
                val app = context.applicationContext as TaskKuApplication
                val container = app.container
                val detailViewModel: TaskDetailViewModel = viewModel(
                    key = "task_detail_${key.taskId}",
                    factory = TaskDetailViewModelFactory(
                        taskId = key.taskId,
                        taskRepository = container.taskRepository,
                        statusRepository = container.statusRepository,
                        appContext = context.applicationContext
                    )
                )
                TaskDetailScreen(
                    taskId = key.taskId,
                    onNavigateBack = handleBack,
                    onEditClick = { taskId -> backStack.add(TaskForm(taskId)) },
                    viewModel = detailViewModel
                )
            }
            entry<TaskForm> { key ->
                val app = context.applicationContext as TaskKuApplication
                val container = app.container
                val parsedTag = key.initialTag?.let { com.example.taskku.domain.model.TaskTag.fromString(it) }
                val formViewModel: TaskFormViewModel = viewModel(
                    key = "task_form_${key.taskId ?: 0}_${key.initialSubject.orEmpty()}_${key.initialDeadlineDate ?: 0}_${key.initialTag.orEmpty()}",
                    factory = TaskFormViewModelFactory(
                        taskId = key.taskId,
                        taskRepository = container.taskRepository,
                        subjectRepository = container.subjectRepository,
                        statusRepository = container.statusRepository,
                        appContext = context.applicationContext,
                        initialSubject = key.initialSubject,
                        initialDeadlineDate = key.initialDeadlineDate,
                        initialTag = parsedTag
                    )
                )
                TaskFormScreen(
                    taskId = key.taskId,
                    onNavigateBack = handleBack,
                    onSaved = handleBack,
                    viewModel = formViewModel
                )
            }
        }
    )
}
