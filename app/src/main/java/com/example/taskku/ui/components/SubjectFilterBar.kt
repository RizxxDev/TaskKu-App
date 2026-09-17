package com.example.taskku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Immutable
data class SubjectWithCount(val name: String, val count: Int)

@Composable
fun SubjectFilterBar(
    subjects: List<SubjectWithCount>,
    selectedSubjects: Set<String>,
    onSubjectToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allCount = remember(subjects) { subjects.sumOf { it.count } }
    val isAllSelected = remember(selectedSubjects) {
        selectedSubjects.isEmpty() || selectedSubjects.contains("Semua")
    }
    val onAllClick = remember(onSubjectToggle) { { onSubjectToggle("Semua") } }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "all", contentType = "subject_chip") {
            SubjectChip(
                name = "Semua",
                count = allCount,
                isSelected = isAllSelected,
                onClick = onAllClick
            )
        }

        items(
            items = subjects,
            key = { it.name },
            contentType = { "subject_chip" }
        ) { subject ->
            SubjectChip(
                name = subject.name,
                count = subject.count,
                isSelected = selectedSubjects.contains(subject.name),
                onClick = { onSubjectToggle(subject.name) }
            )
        }
    }
}
