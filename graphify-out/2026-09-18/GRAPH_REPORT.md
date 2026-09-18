# Graph Report - TaskKu  (2026-09-18)

## Corpus Check
- 100 files · ~37,450 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 24 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 784 nodes · 1566 edges · 45 communities (27 shown, 18 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 56 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d6c247ae`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Task
- MainScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- Composable
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
- AppContainer
- TimetableDao
- TaskDetailViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- TaskListViewModel
- NotificationSchedulerTest
- AppPreferences
- Difficulty
- MemberEntity
- SettingsViewModelTest
- TaskType
- CalendarViewModelTest
- DashboardViewModelTest
- OperationResult
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- WidgetLogicTest.kt
- ExportImportTest

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
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `MainNavigation()` --calls--> `MainScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/main/MainScreen.kt
- `AppPreferences` --references--> `SortOption`  [EXTRACTED]
  app/src/main/java/com/example/taskku/data/preferences/AppPreferences.kt → app/src/main/java/com/example/taskku/domain/model/SortOption.kt

## Import Cycles
- None detected.

## Communities (45 total, 18 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.14
Nodes (5): StateFlow, T, ViewModel, SettingsUiState, SettingsViewModel

### Community 1 - "Task"
Cohesion: 0.07
Nodes (11): Flow, TaskRepository, TaskRepositoryImpl, Attachment, Member, Subtask, Task, FileStorageHelper (+3 more)

### Community 2 - "MainScreen.kt"
Cohesion: 0.07
Nodes (37): androidx, Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+29 more)

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
Cohesion: 0.08
Nodes (6): FormState, StateFlow, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 7 - "Composable"
Cohesion: 0.05
Nodes (47): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, MainNavigation(), DeadlineText(), Modifier (+39 more)

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
Cohesion: 0.10
Nodes (8): Flow, StatusDao, StatusEntity, Flow, StatusRepository, StatusRepositoryImpl, Status, FakeStatusRepository

### Community 17 - "TimetableItem"
Cohesion: 0.05
Nodes (25): Flow, TimetableRepository, TimetableRepositoryImpl, SchoolDay, JUMAT, KAMIS, MINGGU, RABU (+17 more)

### Community 19 - "AppContainer"
Cohesion: 0.23
Nodes (8): AppContainer, com, BootReceiver, BroadcastReceiver, Context, Intent, TaskKuApplication, Application

### Community 20 - "TimetableDao"
Cohesion: 0.26
Nodes (3): Flow, TimetableDao, TimetableEntity

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 23 - "TaskListViewModel"
Cohesion: 0.08
Nodes (11): Modifier, SubjectFilterBar(), SubjectWithCount, FilterParams, Flow, StateFlow, T, ViewModel (+3 more)

### Community 25 - "AppPreferences"
Cohesion: 0.18
Nodes (9): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+1 more)

### Community 26 - "Difficulty"
Cohesion: 0.29
Nodes (4): Difficulty, MUDAH, SEDANG, SULIT

### Community 28 - "SettingsViewModelTest"
Cohesion: 0.20
Nodes (3): ExportImportManager, SettingsViewModelTest, Result

### Community 29 - "TaskType"
Cohesion: 0.29
Nodes (3): TaskType, KELOMPOK, PRIBADI

### Community 32 - "OperationResult"
Cohesion: 0.29
Nodes (5): Error, Idle, InProgress, OperationResult, Success

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

## Knowledge Gaps
- **74 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+69 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 243 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **18 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `MainScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `Composable`, `CalendarViewModel`, `NotificationScheduler`, `WidgetLogicTest.kt`, `FakeTaskRepository`, `TaskDetailViewModelTest`, `TaskListViewModel`, `NotificationSchedulerTest`, `TaskType`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.208) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Task`, `Status`, `SubtaskDao`, `TimetableDao`, `MemberEntity`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `Task` to `MainScreen.kt`, `TaskFormViewModel`, `Composable`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `AppContainer`, `TaskListViewModel`, `SettingsViewModelTest`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _74 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.14166666666666666 - nodes in this community are weakly interconnected._