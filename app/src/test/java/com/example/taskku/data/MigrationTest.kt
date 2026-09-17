package com.example.taskku.data

import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskku.data.local.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Proxy

class MigrationTest {

    @Test
    fun migration1To2_executesAllRequiredIndexStatements() {
        val executedQueries = mutableListOf<String>()
        val mockDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedQueries.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        AppDatabase.MIGRATION_1_2.migrate(mockDb)

        assertEquals(4, executedQueries.size)
        assertEquals("CREATE INDEX IF NOT EXISTS `index_tasks_statusId` ON `tasks` (`statusId`)", executedQueries[0])
        assertEquals("CREATE INDEX IF NOT EXISTS `index_tasks_deadlineDate` ON `tasks` (`deadlineDate`)", executedQueries[1])
        assertEquals("CREATE INDEX IF NOT EXISTS `index_tasks_subject` ON `tasks` (`subject`)", executedQueries[2])
        assertEquals("CREATE INDEX IF NOT EXISTS `index_subjects_isVisible` ON `subjects` (`isVisible`)", executedQueries[3])
    }

    @Test
    fun migration2To3_addsReminderOffsetColumn() {
        val executedQueries = mutableListOf<String>()
        val mockDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedQueries.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        AppDatabase.MIGRATION_2_3.migrate(mockDb)

        assertEquals(1, executedQueries.size)
        assertEquals("ALTER TABLE `tasks` ADD COLUMN `reminderOffset` TEXT NOT NULL DEFAULT 'ONE_HOUR_BEFORE'", executedQueries[0])
    }
}
