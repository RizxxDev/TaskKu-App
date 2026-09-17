package com.example.taskku.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskTest {

    @Test
    fun isCompleted_returnsTrueWhenStatusNameIsSelesaiOrDone() {
        val completedTask1 = Task(
            title = "Tugas 1",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 3,
            statusName = "Selesai"
        )
        assertTrue(completedTask1.isCompleted)

        val completedTask2 = Task(
            title = "Tugas 2",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 3,
            statusName = "selesai"
        )
        assertTrue(completedTask2.isCompleted)

        val completedTask3 = Task(
            title = "Tugas 3",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 3,
            statusName = "Done"
        )
        assertTrue(completedTask3.isCompleted)
    }

    @Test
    fun isCompleted_returnsFalseWhenStatusNameIsNotSelesai() {
        val pendingTask = Task(
            title = "Tugas 1",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            statusName = "Belum Dikerjakan"
        )
        assertFalse(pendingTask.isCompleted)

        val inProgressTask = Task(
            title = "Tugas 2",
            subject = "Matematika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 2,
            statusName = "Sedang Dikerjakan"
        )
        assertFalse(inProgressTask.isCompleted)
    }

    @Test
    fun isOverdue_correctlyFlagsPastDeadlines() {
        val overdueTask = Task(
            title = "Tugas Lewat",
            subject = "Fisika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            deadlineDate = System.currentTimeMillis() - 100000L
        )
        assertTrue(overdueTask.isOverdue)

        val futureTask = Task(
            title = "Tugas Mendatang",
            subject = "Fisika",
            type = TaskType.PRIBADI,
            difficulty = Difficulty.MUDAH,
            statusId = 1,
            deadlineDate = System.currentTimeMillis() + 100000000L
        )
        assertFalse(futureTask.isOverdue)
    }
}
