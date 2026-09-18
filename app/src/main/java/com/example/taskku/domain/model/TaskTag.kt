package com.example.taskku.domain.model

enum class TaskTag(
    val displayName: String,
    val shortName: String,
    val colorHex: String,
    val description: String
) {
    PR("PR", "PR", "#0984E3", "Tugas Harian"),
    KUIS("Kuis", "Kuis", "#E17055", "Kuis / Ulangan"),
    PRAKTIKUM("Praktikum", "Praktikum", "#6C5CE7", "Laporan Praktikum"),
    PROYEK("Proyek", "Proyek", "#00B894", "Proyek / Makalah");

    companion object {
        fun fromString(value: String?): TaskTag =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: PR
    }
}
