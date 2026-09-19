# Graph Report - TaskKu  (2026-09-19)

## Corpus Check
- 103 files · ~38,962 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 24 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 825 nodes · 1644 edges · 51 communities (34 shown, 17 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 58 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `56766aba`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Task
- CalendarScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- TaskTag
- AdaptiveLayout.kt
- NotificationScheduler
- MainActivity.kt
- TaskDao
- DataRepository
- FakeTaskRepository
- TaskDetailViewModel
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SortOption
- TaskRepository
- DashboardScreen.kt
- TaskDetailViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- MemberEntity
- NotificationSchedulerTest
- TaskListViewModel
- Difficulty
- Attachment
- SettingsViewModelTest
- TaskDetailScreen.kt
- CalendarViewModelTest
- DashboardViewModelTest
- AppPreferences
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- Composable
- TimetableViewModel.kt
- ExportImportTest
- isWideDisplay
- StatusBadge
- SettingsViewModelFactory
- MainScreen.kt
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
- `TaskCard()` --calls--> `DeadlineText()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DeadlineText.kt
- `TaskCard()` --calls--> `DifficultyBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DifficultyBadge.kt
- `TaskCard()` --calls--> `StatusBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/StatusBadge.kt

## Import Cycles
- None detected.

## Communities (51 total, 17 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.11
Nodes (9): Error, Idle, InProgress, StateFlow, ViewModel, OperationResult, SettingsUiState, SettingsViewModel (+1 more)

### Community 1 - "Task"
Cohesion: 0.18
Nodes (4): Member, Subtask, Task, TaskTest

### Community 2 - "CalendarScreen.kt"
Cohesion: 0.11
Nodes (23): CalendarCellData, CalendarEmptyCard(), CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier (+15 more)

### Community 3 - "Subject"
Cohesion: 0.09
Nodes (8): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, Subject, FakeSubjectRepository

### Community 4 - "AppDatabase"
Cohesion: 0.07
Nodes (11): AttachmentDao, SubtaskDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, SubtaskEntity, MigrationTest (+3 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.12
Nodes (19): ActionCallback, ActionParameters, BootReceiver, BroadcastReceiver, Context, Intent, TaskKuApplication, formatWidgetDeadline() (+11 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.07
Nodes (9): android, FormState, Context, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel (+1 more)

### Community 7 - "TaskTag"
Cohesion: 0.17
Nodes (11): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, getTagIcon(), ImageVector, Modifier (+3 more)

### Community 8 - "AdaptiveLayout.kt"
Cohesion: 0.20
Nodes (11): isCompactHeight(), rememberWindowHeightSizeClass(), rememberWindowWidthSizeClass(), WindowHeightSizeClass, COMPACT, EXPANDED, MEDIUM, WindowWidthSizeClass (+3 more)

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

### Community 14 - "TaskDetailViewModel"
Cohesion: 0.17
Nodes (10): MainNavigation(), Factory, StateFlow, T, ViewModel, TaskDetailViewModel, TaskDetailViewModelFactory, UiState (+2 more)

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.09
Nodes (10): Flow, StatusDao, StatusEntity, Flow, StatusRepository, StatusRepositoryImpl, AppContainer, com (+2 more)

### Community 17 - "TimetableItem"
Cohesion: 0.05
Nodes (22): Flow, TimetableDao, TimetableEntity, Flow, TimetableRepository, TimetableRepositoryImpl, SchoolDay, JUMAT (+14 more)

### Community 18 - "SortOption"
Cohesion: 0.19
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 19 - "TaskRepository"
Cohesion: 0.10
Nodes (6): StatusCount, Flow, TaskRepository, TaskRepositoryImpl, Context, TaskKuWidgetHelper

### Community 20 - "DashboardScreen.kt"
Cohesion: 0.35
Nodes (11): DashboardScreen(), Color, ImageVector, Modifier, NextClassCard(), QuickStatsSection(), StatCard(), UrgentEmptyCard() (+3 more)

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 25 - "TaskListViewModel"
Cohesion: 0.07
Nodes (14): Modifier, SubjectFilterBar(), SubjectWithCount, FilteredData, FilterParams, Factory, Flow, StateFlow (+6 more)

### Community 26 - "Difficulty"
Cohesion: 0.10
Nodes (8): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, WidgetLogicTest

### Community 27 - "Attachment"
Cohesion: 0.38
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 29 - "TaskDetailScreen.kt"
Cohesion: 0.29
Nodes (8): DeadlineText(), Modifier, DifficultyBadge(), com, Modifier, Modifier, SubtaskItemRow(), TaskDetailScreen()

### Community 32 - "AppPreferences"
Cohesion: 0.19
Nodes (9): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+1 more)

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 43 - "Composable"
Cohesion: 0.23
Nodes (12): androidx, Modifier, SubjectChip(), AddStatusDialog(), AddSubjectDialog(), EditStatusDialog(), Modifier, PresetColor (+4 more)

### Community 44 - "TimetableViewModel.kt"
Cohesion: 0.11
Nodes (16): DashboardViewModel, DashboardViewModelFactory, Factory, StateFlow, T, ViewModel, UiState, Factory (+8 more)

### Community 45 - "ExportImportTest"
Cohesion: 0.20
Nodes (4): ExportImportManager, ExportImportTest, com, Result

### Community 46 - "isWideDisplay"
Cohesion: 0.48
Nodes (5): Modifier, TaskCard(), Modifier, TaskListScreen(), isWideDisplay()

### Community 47 - "StatusBadge"
Cohesion: 0.70
Nodes (4): Color, Modifier, parseStatusColor(), StatusBadge()

### Community 48 - "SettingsViewModelFactory"
Cohesion: 0.50
Nodes (3): Factory, T, SettingsViewModelFactory

### Community 52 - "MainScreen.kt"
Cohesion: 0.19
Nodes (14): Main, TaskDetail, TaskForm, AnimatedNavIcon(), ImageVector, Modifier, MainScreen(), NavigationTab (+6 more)

## Knowledge Gaps
- **81 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+76 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 257 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `CalendarScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `NotificationScheduler`, `TimetableViewModel.kt`, `FakeTaskRepository`, `isWideDisplay`, `TaskDetailViewModel`, `TaskRepository`, `DashboardScreen.kt`, `TaskDetailViewModelTest`, `NotificationSchedulerTest`, `TaskListViewModel`, `Difficulty`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.207) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `TaskRepository`, `MemberEntity`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Why does `TimetableItem` connect `TimetableItem` to `DashboardScreen.kt`, `TaskKuWidget.kt`, `TimetableViewModel.kt`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _81 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.10822510822510822 - nodes in this community are weakly interconnected._