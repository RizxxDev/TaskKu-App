# Graph Report - TaskKu  (2026-09-17)

## Corpus Check
- 91 files · ~30,330 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 23 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 673 nodes · 1309 edges · 36 communities (19 shown, 17 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 47 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `901adadb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- AppPreferences
- 🌟 Fitur Unggulan
- AttachmentEntity
- SubtaskDao
- TaskListViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- NotificationSchedulerTest
- TaskDetailViewModelTest
- Difficulty
- FakeStatusRepository
- CalendarViewModelTest
- DashboardViewModelTest
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
- `TaskKuApplication` --references--> `AppContainer`  [EXTRACTED]
  app/src/main/java/com/example/taskku/TaskKuApplication.kt → app/src/main/java/com/example/taskku/di/AppContainer.kt

## Import Cycles
- None detected.

## Communities (36 total, 17 thin omitted)

### Community 0 - "Status"
Cohesion: 0.06
Nodes (22): androidx, Flow, StatusRepository, StatusRepositoryImpl, Status, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog() (+14 more)

### Community 1 - "Task"
Cohesion: 0.06
Nodes (13): Flow, TaskRepository, TaskRepositoryImpl, Attachment, Member, Subtask, Task, FileStorageHelper (+5 more)

### Community 2 - "MainScreen.kt"
Cohesion: 0.06
Nodes (37): MainNavigation(), Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+29 more)

### Community 3 - "Subject"
Cohesion: 0.08
Nodes (9): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, AppContainer, com (+1 more)

### Community 4 - "AppDatabase"
Cohesion: 0.08
Nodes (12): MemberDao, Flow, StatusDao, AppDatabase, AppDatabaseCallback, Context, MemberEntity, StatusEntity (+4 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.11
Nodes (21): ActionCallback, ActionParameters, BootReceiver, BroadcastReceiver, Context, Intent, TaskKuApplication, formatWidgetDeadline() (+13 more)

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
Cohesion: 0.16
Nodes (5): Flow, StatusCount, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 14 - "AppPreferences"
Cohesion: 0.06
Nodes (28): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+20 more)

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 26 - "Difficulty"
Cohesion: 0.14
Nodes (7): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI

### Community 28 - "FakeStatusRepository"
Cohesion: 0.13
Nodes (6): ExportImportManager, ExportImportTest, com, FakeStatusRepository, FakeSubjectRepository, SettingsViewModelTest

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

## Knowledge Gaps
- **61 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+56 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 212 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `MainScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `Composable`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `AppPreferences`, `TaskListViewModelTest`, `NotificationSchedulerTest`, `TaskDetailViewModelTest`, `Difficulty`, `FakeStatusRepository`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.236) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `Task` to `MainScreen.kt`, `Subject`, `TaskFormViewModel`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `AppPreferences`, `FakeStatusRepository`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `Task`, `Subject`, `AttachmentEntity`, `SubtaskDao`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 6 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _61 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Status` be split into smaller, more focused modules?**
  _Cohesion score 0.06431372549019608 - nodes in this community are weakly interconnected._