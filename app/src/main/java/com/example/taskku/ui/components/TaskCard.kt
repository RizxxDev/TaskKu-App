package com.example.taskku.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskku.domain.model.Task
import com.example.taskku.domain.model.TaskType

private val TaskCardShape = RoundedCornerShape(16.dp)
private val TaskCardElevation = 2.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: ((Long) -> Unit)? = null,
    onLongClick: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onDeleteClick != null) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onDeleteClick(task.id)
                    true
                } else {
                    false
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                        .clip(TaskCardShape)
                        .background(MaterialTheme.colorScheme.error)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Tugas",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            },
            enableDismissFromStartToEnd = false,
            modifier = modifier
        ) {
            TaskCardContent(
                task = task,
                onTaskClick = onTaskClick,
                onLongClick = onLongClick,
                modifier = Modifier
            )
        }
    } else {
        TaskCardContent(
            task = task,
            onTaskClick = onTaskClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCardContent(
    task: Task,
    onTaskClick: (Long) -> Unit,
    onLongClick: ((Long) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val clickAction = remember(task.id, onTaskClick) { { onTaskClick(task.id) } }
    val longClickAction = remember(task.id, onLongClick) {
        if (onLongClick != null) { { onLongClick(task.id) } } else null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(TaskCardShape)
            .combinedClickable(
                onClick = clickAction,
                onLongClick = longClickAction
            ),
        shape = TaskCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = TaskCardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                DifficultyBadge(difficulty = task.difficulty)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    TaskTagBadge(tag = task.tag)
                    Text(
                        text = task.subject,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(statusName = task.statusName, colorHex = task.statusColorHex)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    DeadlineText(deadlineMillis = task.deadlineMillis)
                }
                Spacer(modifier = Modifier.width(8.dp))
                val isKelompok = task.type == TaskType.KELOMPOK
                val typeText = remember(task.type, task.memberCount) {
                    if (isKelompok) "Kelompok (${task.memberCount})" else "Pribadi"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (isKelompok) Icons.Outlined.Group else Icons.Outlined.Person,
                        contentDescription = "Tipe Tugas",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = typeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
