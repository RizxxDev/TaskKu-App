# Graph Report - TaskKu  (2026-09-18)

## Corpus Check
- 100 files · ~37,191 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 23 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 782 nodes · 1563 edges · 51 communities (31 shown, 20 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 56 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `33c5dceb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Attachment
- MainScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- TaskTag
- CalendarViewModel
- NotificationScheduler
- MainActivity.kt
- TaskDao
- DataRepository
- FakeTaskRepository
- SortOption
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SubtaskDao
- BootReceiver.kt
- TimetableDao
- SettingsScreen.kt
- 2. Guardrails & Aturan Kritis Pengembangan
- TaskListViewModel
- NotificationSchedulerTest
- AppPreferences
- Difficulty
- MemberEntity
- SettingsViewModelTest
- TaskRepository
- CalendarViewModelTest
- DashboardViewModelTest
- TaskListViewModelTest
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- TaskRepositoryImpl
- TaskDetailScreen.kt
- DashboardScreen.kt
- FileStorageHelper
- ExportImportTest
- Composable
- Task
- TaskCard

## God Nodes (most connected - your core abstractions)
1. `Task` - 66 edges
2. `TimetableItem` - 41 edges
3. `TaskFormViewModel` - 38 edges
4. `FakeTaskRepository` - 32 edges
5. `TaskRepository` - 28 edges
6. `Subject` - 24 edges
7. `Status` - 23 edges
8. `TaskRepositoryImpl` - 20 edges
9. `SettingsViewModel` - 20 edges
10. `TaskListViewModel` - 20 edges

## Surprising Connections (you probably didn't know these)
- `TaskCard()` --calls--> `DeadlineText()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DeadlineText.kt
- `TaskCard()` --calls--> `DifficultyBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DifficultyBadge.kt
- `TaskCard()` --calls--> `StatusBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/StatusBadge.kt
- `TaskCard()` --calls--> `TaskTagBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/TaskTagBadge.kt
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt

## Import Cycles
- None detected.

## Communities (51 total, 20 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.10
Nodes (10): Error, Idle, InProgress, StateFlow, T, ViewModel, OperationResult, SettingsUiState (+2 more)

### Community 1 - "Attachment"
Cohesion: 0.20
Nodes (3): Attachment, Member, Subtask

### Community 2 - "MainScreen.kt"
Cohesion: 0.05
Nodes (39): MainNavigation(), Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+31 more)

### Community 3 - "Subject"
Cohesion: 0.09
Nodes (8): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, Subject, FakeSubjectRepository

### Community 4 - "AppDatabase"
Cohesion: 0.10
Nodes (9): AttachmentDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, MigrationTest, Callback, RoomDatabase (+1 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.23
Nodes (13): ActionCallback, ActionParameters, formatWidgetDeadline(), Context, GlanceAppWidget, TaskItemRow(), TaskKuWidget, ToggleTaskActionCallback (+5 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.07
Nodes (7): FormState, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 7 - "TaskTag"
Cohesion: 0.17
Nodes (11): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, getTagIcon(), ImageVector, Modifier (+3 more)

### Community 8 - "CalendarViewModel"
Cohesion: 0.12
Nodes (18): CalendarCellData, CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier, LegendItem() (+10 more)

### Community 9 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 10 - "MainActivity.kt"
Cohesion: 0.15
Nodes (10): MainScreenTest, Intent, MainActivity, BroadcastReceiver, Context, Intent, NotificationReceiver, TaskKuTheme() (+2 more)

### Community 11 - "TaskDao"
Cohesion: 0.18
Nodes (5): Flow, StatusCount, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 14 - "SortOption"
Cohesion: 0.19
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.08
Nodes (12): Flow, StatusDao, StatusEntity, Flow, StatusRepository, StatusRepositoryImpl, AppContainer, com (+4 more)

### Community 17 - "TimetableItem"
Cohesion: 0.05
Nodes (25): Flow, TimetableRepository, TimetableRepositoryImpl, SchoolDay, JUMAT, KAMIS, MINGGU, RABU (+17 more)

### Community 19 - "BootReceiver.kt"
Cohesion: 0.53
Nodes (4): BootReceiver, BroadcastReceiver, Context, Intent

### Community 20 - "TimetableDao"
Cohesion: 0.26
Nodes (3): Flow, TimetableDao, TimetableEntity

### Community 21 - "SettingsScreen.kt"
Cohesion: 0.36
Nodes (9): androidx, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog(), Modifier, PresetColor, SectionCard(), SettingsScreen() (+1 more)

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 23 - "TaskListViewModel"
Cohesion: 0.18
Nodes (7): FilterParams, Flow, StateFlow, T, ViewModel, TaskListViewModel, UiState

### Community 25 - "AppPreferences"
Cohesion: 0.18
Nodes (9): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+1 more)

### Community 26 - "Difficulty"
Cohesion: 0.10
Nodes (8): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, WidgetLogicTest

### Community 28 - "SettingsViewModelTest"
Cohesion: 0.20
Nodes (3): ExportImportManager, SettingsViewModelTest, Result

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 44 - "TaskDetailScreen.kt"
Cohesion: 0.29
Nodes (8): DifficultyBadge(), com, Modifier, Modifier, StatusBadge(), Modifier, SubtaskItemRow(), TaskDetailScreen()

### Community 45 - "DashboardScreen.kt"
Cohesion: 0.40
Nodes (8): DeadlineText(), Modifier, DashboardScreen(), Color, ImageVector, Modifier, StatCard(), UrgentTaskCard()

### Community 46 - "FileStorageHelper"
Cohesion: 0.40
Nodes (3): FileStorageHelper, Context, Uri

### Community 48 - "Composable"
Cohesion: 0.33
Nodes (6): Modifier, SubjectChip(), Modifier, SubjectFilterBar(), SubjectWithCount, Composable

### Community 50 - "TaskCard"
Cohesion: 0.53
Nodes (4): Modifier, TaskCard(), Modifier, TaskListScreen()

## Knowledge Gaps
- **74 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+69 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 243 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **20 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `TaskListViewModelTest`, `Attachment`, `MainScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `CalendarViewModel`, `NotificationScheduler`, `TaskRepositoryImpl`, `DashboardScreen.kt`, `FakeTaskRepository`, `TaskCard`, `TaskListViewModel`, `NotificationSchedulerTest`, `Difficulty`, `TaskRepository`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.208) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `SubtaskDao`, `TimetableDao`, `MemberEntity`, `TaskRepository`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `TaskRepository` to `Attachment`, `MainScreen.kt`, `TaskFormViewModel`, `CalendarViewModel`, `NotificationScheduler`, `TaskRepositoryImpl`, `FakeTaskRepository`, `Status`, `TaskListViewModel`, `SettingsViewModelTest`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _74 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.10276679841897234 - nodes in this community are weakly interconnected._