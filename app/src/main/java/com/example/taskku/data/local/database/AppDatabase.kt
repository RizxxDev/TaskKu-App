package com.example.taskku.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskku.data.local.dao.*
import com.example.taskku.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        MemberEntity::class,
        SubtaskEntity::class,
        AttachmentEntity::class,
        StatusEntity::class,
        SubjectEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun memberDao(): MemberDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun statusDao(): StatusDao
    abstract fun subjectDao(): SubjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_statusId` ON `tasks` (`statusId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_deadlineDate` ON `tasks` (`deadlineDate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_subject` ON `tasks` (`subject`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subjects_isVisible` ON `subjects` (`isVisible`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `reminderOffset` TEXT NOT NULL DEFAULT 'ONE_HOUR_BEFORE'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taskku_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val statusDao = database.statusDao()
            statusDao.insertStatus(StatusEntity(name = "Belum Dikerjakan", colorHex = "#B2BEC3", sortOrder = 1, isDefault = true))
            statusDao.insertStatus(StatusEntity(name = "Sedang Dikerjakan", colorHex = "#74B9FF", sortOrder = 2, isDefault = true))
            statusDao.insertStatus(StatusEntity(name = "Selesai", colorHex = "#55EFC4", sortOrder = 3, isDefault = true))

            val subjectDao = database.subjectDao()
            val presetSubjects = listOf(
                "Matematika", "Fisika", "Kimia", "Biologi", 
                "B. Indonesia", "B. Inggris", "Sejarah", 
                "Ekonomi", "Sosiologi", "Geografi", 
                "Seni Budaya", "PJOK", "Informatika", "PAI", "PKN"
            )
            presetSubjects.forEach {
                subjectDao.insertSubject(SubjectEntity(name = it, isPreset = true, isVisible = true))
            }
        }
    }
}
