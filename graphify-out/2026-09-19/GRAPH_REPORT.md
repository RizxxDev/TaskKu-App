# Graph Report - TaskKu  (2026-09-19)

## Corpus Check
- 103 files · ~38,671 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 24 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 813 nodes · 1623 edges · 55 communities (33 shown, 22 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 58 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b2e31c88`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Subtask
- CalendarScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- DashboardScreen.kt
- TaskListViewModelTest
- NotificationScheduler
- MainActivity.kt
- TaskDao
- DataRepository
- FakeTaskRepository
- MainNavigation
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SortOption
- TaskRepository
- Task
- TaskDetailViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- MemberEntity
- NotificationSchedulerTest
- TaskListViewModel
- Difficulty
- Attachment
- SettingsViewModelTest
- AttachmentEntity
- CalendarViewModelTest
- DashboardViewModelTest
- AppPreferences
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- SettingsScreen.kt
- TimetableViewModel.kt
- SubjectFilterBar
- StatusRepositoryImpl
- MainScreen.kt
- TaskRepositoryImpl
- TaskDetailViewModel
- SortDirection
- DashboardViewModel.kt
- NavigationTab
- NavKey
- AdaptiveLayoutTest

## God Nodes (most connected - your core abstractions)
1. `Task` - 67 edges
2. `TimetableItem` - 43 edges
3. `TaskFormViewModel` - 39 edges
4. `FakeTaskRepository` - 32 edges
5. `TaskRepository` - 28 edges
6. `Subject` - 24 edges
7. `Status` - 23 edges
8. `TaskListViewModel` - 21 edges
9. `TaskRepositoryImpl` - 20 edges
10. `SettingsViewModel` - 20 edges

## Surprising Connections (you probably didn't know these)
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `MainNavigation()` --calls--> `MainScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/main/MainScreen.kt
- `MainNavigation()` --calls--> `TaskDetailScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/taskdetail/TaskDetailScreen.kt

## Import Cycles
- None detected.

## Communities (55 total, 22 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.11
Nodes (12): Error, Idle, InProgress, Factory, StateFlow, T, ViewModel, OperationResult (+4 more)

### Community 2 - "CalendarScreen.kt"
Cohesion: 0.13
Nodes (20): CalendarCellData, CalendarEmptyCard(), CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier (+12 more)

### Community 3 - "Subject"
Cohesion: 0.07
Nodes (10): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, Subject, ExportImportTest (+2 more)

### Community 4 - "AppDatabase"
Cohesion: 0.07
Nodes (12): Flow, StatusDao, SubtaskDao, AppDatabase, AppDatabaseCallback, Context, StatusEntity, SubtaskEntity (+4 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.10
Nodes (23): ActionCallback, ActionParameters, AppContainer, com, BootReceiver, BroadcastReceiver, Context, Intent (+15 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.07
Nodes (9): android, FormState, Context, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel (+1 more)

### Community 7 - "DashboardScreen.kt"
Cohesion: 0.06
Nodes (50): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, DeadlineText(), Modifier, DifficultyBadge() (+42 more)

### Community 9 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 10 - "MainActivity.kt"
Cohesion: 0.15
Nodes (10): MainScreenTest, Intent, MainActivity, BroadcastReceiver, Context, Intent, NotificationReceiver, TaskKuTheme() (+2 more)

### Community 11 - "TaskDao"
Cohesion: 0.19
Nodes (4): Flow, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 14 - "MainNavigation"
Cohesion: 0.32
Nodes (6): MainNavigation(), Factory, T, TaskDetailViewModelFactory, Factory, TaskFormViewModelFactory

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.17
Nodes (4): Flow, StatusRepository, Status, FakeStatusRepository

### Community 17 - "TimetableItem"
Cohesion: 0.06
Nodes (22): Flow, TimetableDao, TimetableEntity, Flow, TimetableRepository, TimetableRepositoryImpl, SchoolDay, JUMAT (+14 more)

### Community 18 - "SortOption"
Cohesion: 0.21
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 19 - "TaskRepository"
Cohesion: 0.16
Nodes (3): StatusCount, Flow, TaskRepository

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 25 - "TaskListViewModel"
Cohesion: 0.15
Nodes (10): FilteredData, FilterParams, Factory, Flow, StateFlow, T, ViewModel, TaskListViewModel (+2 more)

### Community 26 - "Difficulty"
Cohesion: 0.10
Nodes (8): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, WidgetLogicTest

### Community 27 - "Attachment"
Cohesion: 0.33
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 28 - "SettingsViewModelTest"
Cohesion: 0.20
Nodes (3): ExportImportManager, SettingsViewModelTest, Result

### Community 32 - "AppPreferences"
Cohesion: 0.25
Nodes (6): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 43 - "SettingsScreen.kt"
Cohesion: 0.36
Nodes (9): androidx, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog(), Modifier, PresetColor, SectionCard(), SettingsScreen() (+1 more)

### Community 44 - "TimetableViewModel.kt"
Cohesion: 0.24
Nodes (6): StateFlow, ViewModel, TimetableViewModel, UiState, DispatcherProvider, CoroutineDispatcher

### Community 45 - "SubjectFilterBar"
Cohesion: 0.60
Nodes (3): Modifier, SubjectFilterBar(), SubjectWithCount

### Community 47 - "MainScreen.kt"
Cohesion: 0.31
Nodes (7): DashboardViewModelFactory, Factory, Modifier, MainScreen(), Factory, T, TimetableViewModelFactory

### Community 49 - "TaskDetailViewModel"
Cohesion: 0.32
Nodes (4): StateFlow, ViewModel, TaskDetailViewModel, UiState

### Community 50 - "SortDirection"
Cohesion: 0.29
Nodes (3): SortDirection, ASC, DESC

### Community 51 - "DashboardViewModel.kt"
Cohesion: 0.38
Nodes (5): DashboardViewModel, StateFlow, T, ViewModel, UiState

### Community 52 - "NavigationTab"
Cohesion: 0.33
Nodes (6): NavigationTab, CALENDAR, DASHBOARD, SETTINGS, TASKS, TIMETABLE

### Community 53 - "NavKey"
Cohesion: 0.70
Nodes (4): Main, TaskDetail, TaskForm, NavKey

## Knowledge Gaps
- **78 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+73 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 251 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **22 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `Subtask`, `CalendarScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `DashboardScreen.kt`, `TaskListViewModelTest`, `NotificationScheduler`, `FakeTaskRepository`, `TaskRepositoryImpl`, `TaskDetailViewModel`, `SortDirection`, `TaskRepository`, `DashboardViewModel.kt`, `NotificationSchedulerTest`, `TaskListViewModel`, `Difficulty`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.211) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `TaskRepositoryImpl`, `TaskRepository`, `MemberEntity`, `AttachmentEntity`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **Why does `TimetableItem` connect `TimetableItem` to `TaskKuWidget.kt`, `DashboardScreen.kt`, `TimetableViewModel.kt`, `FakeTaskRepository`, `DashboardViewModel.kt`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _78 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.1067193675889328 - nodes in this community are weakly interconnected._