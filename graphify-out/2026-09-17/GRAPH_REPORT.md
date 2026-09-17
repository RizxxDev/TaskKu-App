# Graph Report - TaskKu  (2026-09-17)

## Corpus Check
- 90 files · ~29,227 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .xml 15, .log 7, (none) 2)

## Summary
- 652 nodes · 1289 edges · 43 communities (26 shown, 17 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 47 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- FakeTaskRepository
- Composable
- Subject
- AppDatabase
- Difficulty
- MainScreen.kt
- Status
- TaskFormViewModel
- MainActivity.kt
- Task
- CalendarViewModel
- TaskDao
- DataRepository
- TaskKuWidget.kt
- NotificationScheduler
- TaskListViewModel
- SubtaskDao
- StatusDao
- TaskType
- AppPreferences
- SubjectDao
- SortOption
- SettingsViewModelTest
- FileStorageHelperTest
- gradlew
- Attachment
- CalendarViewModelTest
- ic_launcher.webp (Launcher Icon)
- 2. Guardrails & Aturan Kritis Pengembangan
- DashboardViewModelTest
- NotificationSchedulerTest
- TaskDetailViewModelTest
- SortDirection
- TaskListViewModelTest
- rules/graphify.md
- workflows/graphify.md
- WidgetLogicTest.kt
- CLAUDE.md
- TaskListScreen

## God Nodes (most connected - your core abstractions)
1. `Task` - 66 edges
2. `TaskFormViewModel` - 35 edges
3. `FakeTaskRepository` - 32 edges
4. `TaskRepository` - 28 edges
5. `Status` - 23 edges
6. `Subject` - 23 edges
7. `TaskRepositoryImpl` - 20 edges
8. `SettingsViewModel` - 20 edges
9. `AppPreferences` - 19 edges
10. `TaskListViewModel` - 19 edges

## Surprising Connections (you probably didn't know these)
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskDetailScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/taskdetail/TaskDetailScreen.kt
- `AppPreferences` --references--> `SortDirection`  [EXTRACTED]
  app/src/main/java/com/example/taskku/data/preferences/AppPreferences.kt → app/src/main/java/com/example/taskku/domain/model/SortDirection.kt

## Import Cycles
- None detected.

## Communities (43 total, 17 thin omitted)

### Community 0 - "FakeTaskRepository"
Cohesion: 0.18
Nodes (3): StatusCount, FakeTaskRepository, Flow

### Community 1 - "Composable"
Cohesion: 0.14
Nodes (20): DeadlineText(), Modifier, DifficultyBadge(), com, Modifier, Modifier, StatusBadge(), Modifier (+12 more)

### Community 2 - "Subject"
Cohesion: 0.08
Nodes (9): Flow, SubjectRepository, SubjectRepositoryImpl, Subject, ExportImportManager, ExportImportTest, com, FakeSubjectRepository (+1 more)

### Community 3 - "AppDatabase"
Cohesion: 0.08
Nodes (11): AttachmentDao, MemberDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, MemberEntity, MigrationTest (+3 more)

### Community 4 - "Difficulty"
Cohesion: 0.29
Nodes (4): Difficulty, MUDAH, SEDANG, SULIT

### Community 5 - "MainScreen.kt"
Cohesion: 0.07
Nodes (36): MainNavigation(), Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+28 more)

### Community 6 - "Status"
Cohesion: 0.06
Nodes (24): androidx, Flow, StatusRepository, StatusRepositoryImpl, Status, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog() (+16 more)

### Community 7 - "TaskFormViewModel"
Cohesion: 0.08
Nodes (7): FormState, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 8 - "MainActivity.kt"
Cohesion: 0.15
Nodes (10): MainScreenTest, Intent, MainActivity, BroadcastReceiver, Context, Intent, NotificationReceiver, TaskKuTheme() (+2 more)

### Community 9 - "Task"
Cohesion: 0.09
Nodes (7): Flow, TaskRepository, TaskRepositoryImpl, Member, Subtask, Task, TaskTest

### Community 10 - "CalendarViewModel"
Cohesion: 0.12
Nodes (18): CalendarCellData, CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier, LegendItem() (+10 more)

### Community 11 - "TaskDao"
Cohesion: 0.19
Nodes (4): Flow, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 13 - "TaskKuWidget.kt"
Cohesion: 0.10
Nodes (23): ActionCallback, ActionParameters, AppContainer, com, BootReceiver, BroadcastReceiver, Context, Intent (+15 more)

### Community 14 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 15 - "TaskListViewModel"
Cohesion: 0.18
Nodes (7): FilterParams, Flow, StateFlow, T, ViewModel, TaskListViewModel, UiState

### Community 17 - "StatusDao"
Cohesion: 0.24
Nodes (3): Flow, StatusDao, StatusEntity

### Community 18 - "TaskType"
Cohesion: 0.29
Nodes (3): TaskType, KELOMPOK, PRIBADI

### Community 19 - "AppPreferences"
Cohesion: 0.21
Nodes (6): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM

### Community 20 - "SubjectDao"
Cohesion: 0.27
Nodes (3): Flow, SubjectDao, SubjectEntity

### Community 21 - "SortOption"
Cohesion: 0.24
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 24 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 25 - "Attachment"
Cohesion: 0.38
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 31 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 36 - "SortDirection"
Cohesion: 0.33
Nodes (3): SortDirection, ASC, DESC

### Community 42 - "TaskListScreen"
Cohesion: 0.39
Nodes (5): Modifier, SubjectFilterBar(), SubjectWithCount, Modifier, TaskListScreen()

## Knowledge Gaps
- **45 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+40 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 195 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `FakeTaskRepository`, `Composable`, `Subject`, `NotificationSchedulerTest`, `DashboardViewModelTest`, `MainScreen.kt`, `TaskDetailViewModelTest`, `TaskFormViewModel`, `SortDirection`, `TaskListViewModelTest`, `CalendarViewModel`, `WidgetLogicTest.kt`, `TaskKuWidget.kt`, `NotificationScheduler`, `TaskListViewModel`, `TaskType`, `CalendarViewModelTest`?**
  _High betweenness centrality (0.252) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `Task` to `FakeTaskRepository`, `Subject`, `MainScreen.kt`, `TaskFormViewModel`, `CalendarViewModel`, `TaskKuWidget.kt`, `NotificationScheduler`, `TaskListViewModel`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `SubtaskDao`, `Task`, `Status`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 6 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _45 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Composable` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._