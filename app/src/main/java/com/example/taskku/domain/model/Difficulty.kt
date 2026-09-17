package com.example.taskku.domain.model

enum class Difficulty(val displayName: String, val colorHex: String) {
    MUDAH("Mudah", "#00B894"),
    SEDANG("Sedang", "#FDCB6E"),
    SULIT("Sulit", "#D63031");

    companion object {
        fun fromString(value: String): Difficulty = 
            values().find { it.name.equals(value, ignoreCase = true) } ?: SEDANG
    }
}
