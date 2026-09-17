# Graph Report - TaskKu  (2026-09-17)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 652 nodes · 1289 edges · 43 communities (26 shown, 17 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 47 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Status
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
- TaskListViewModel
- StatusDao
- AppPreferences
- SubjectDao
- SubtaskDao
- Attachment
- SortOption
- TaskListViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- TaskListScreen
- NotificationSchedulerTest
- TaskDetailViewModelTest
- Difficulty
- TaskType
- SettingsViewModelTest
- SortDirection
- CalendarViewModelTest
- DashboardViewModelTest
- WidgetLogicTest.kt
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)

## God Nodes (most connected - your core abstractions)
1. `Task` - 66 edges
2. `TaskFormViewModel` - 35 edges
3. `FakeTaskRepository` - 32 edges
4. `TaskRepository` - 28 edges
5. `Status` - 23 edges
6. `Subject` - 23 edges
7. `SettingsViewModel` - 20 edges
8. `TaskRepositoryImpl` - 20 edges
9. `TaskListViewModel` - 19 edges
10. `AppPreferences` - 19 edges

## Surprising Connections (you probably didn't know these)
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `AppContainer` --references--> `StatusRepository`  [EXTRACTED]
  app/src/main/java/com/example/taskku/di/AppContainer.kt → app/src/main/java/com/example/taskku/data/repository/StatusRepository.kt
- `AppContainer` --calls--> `StatusRepositoryImpl`  [EXTRACTED]
  app/src/main/java/com/example/taskku/di/AppContainer.kt → app/src/main/java/com/example/taskku/data/repository/StatusRepository.kt
- `SettingsViewModelTest` --references--> `SettingsViewModel`  [EXTRACTED]
  app/src/test/java/com/example/taskku/ui/settings/SettingsViewModelTest.kt → app/src/main/java/com/example/taskku/ui/settings/SettingsViewModel.kt
- `ExportImportTest` --references--> `FakeStatusRepository`  [EXTRACTED]
  app/src/test/java/com/example/taskku/export/ExportImportTest.kt → app/src/test/java/com/example/taskku/fakes/FakeRepositories.kt

## Import Cycles
- None detected.

## Communities (43 total, 17 thin omitted)

### Community 0 - "Status"
Cohesion: 0.06
Nodes (24): androidx, Flow, StatusRepository, StatusRepositoryImpl, Status, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog() (+16 more)

### Community 1 - "Task"
Cohesion: 0.09
Nodes (7): Flow, TaskRepository, TaskRepositoryImpl, Member, Subtask, Task, TaskTest

### Community 2 - "MainScreen.kt"
Cohesion: 0.07
Nodes (36): MainNavigation(), Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+28 more)

### Community 3 - "Subject"
Cohesion: 0.08
Nodes (9): Flow, SubjectRepository, SubjectRepositoryImpl, Subject, ExportImportManager, ExportImportTest, com, FakeSubjectRepository (+1 more)

### Community 4 - "AppDatabase"
Cohesion: 0.08
Nodes (11): AttachmentDao, MemberDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, MemberEntity, MigrationTest (+3 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.10
Nodes (23): ActionCallback, ActionParameters, AppContainer, com, BootReceiver, BroadcastReceiver, Context, Intent (+15 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.08
Nodes (7): FormState, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 7 - "Composable"
Cohesion: 0.14
Nodes (20): DeadlineText(), Modifier, DifficultyBadge(), com, Modifier, Modifier, StatusBadge(), Modifier (+12 more)

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
Cohesion: 0.19
Nodes (4): Flow, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 13 - "FakeTaskRepository"
Cohesion: 0.18
Nodes (3): StatusCount, FakeTaskRepository, Flow

### Community 14 - "TaskListViewModel"
Cohesion: 0.18
Nodes (7): FilterParams, Flow, StateFlow, T, ViewModel, TaskListViewModel, UiState

### Community 15 - "StatusDao"
Cohesion: 0.24
Nodes (3): Flow, StatusDao, StatusEntity

### Community 16 - "AppPreferences"
Cohesion: 0.21
Nodes (6): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM

### Community 17 - "SubjectDao"
Cohesion: 0.27
Nodes (3): Flow, SubjectDao, SubjectEntity

### Community 19 - "Attachment"
Cohesion: 0.38
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 20 - "SortOption"
Cohesion: 0.24
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 23 - "TaskListScreen"
Cohesion: 0.39
Nodes (5): Modifier, SubjectFilterBar(), SubjectWithCount, Modifier, TaskListScreen()

### Community 26 - "Difficulty"
Cohesion: 0.29
Nodes (4): Difficulty, MUDAH, SEDANG, SULIT

### Community 27 - "TaskType"
Cohesion: 0.29
Nodes (3): TaskType, KELOMPOK, PRIBADI

### Community 29 - "SortDirection"
Cohesion: 0.33
Nodes (3): SortDirection, ASC, DESC

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

## Knowledge Gaps
- **45 isolated node(s):** `PresetColor`, `Error`, `Idle`, `InProgress`, `Error` (+40 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 195 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `WidgetLogicTest.kt`, `MainScreen.kt`, `Subject`, `TaskKuWidget.kt`, `TaskFormViewModel`, `Composable`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `TaskListViewModel`, `TaskListViewModelTest`, `NotificationSchedulerTest`, `TaskDetailViewModelTest`, `TaskType`, `SortDirection`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.252) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `Task` to `MainScreen.kt`, `Subject`, `TaskKuWidget.kt`, `TaskFormViewModel`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `TaskListViewModel`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `Task`, `SubtaskDao`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 6 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `PresetColor`, `Error`, `Idle` to the rest of the system?**
  _45 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Status` be split into smaller, more focused modules?**
  _Cohesion score 0.05764411027568922 - nodes in this community are weakly interconnected._