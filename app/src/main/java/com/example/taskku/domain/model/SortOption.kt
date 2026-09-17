package com.example.taskku.domain.model

enum class SortOption(val label: String, val defaultDirection: SortDirection) {
    DEADLINE("Deadline", SortDirection.ASC),
    DIFFICULTY("Kesulitan", SortDirection.DESC),
    CREATED_DATE("Terbaru", SortDirection.DESC),
    SUBJECT("Mapel", SortDirection.ASC)
}
