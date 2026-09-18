package com.example.taskku.ui.timetable

import com.example.taskku.domain.model.SchoolDay
import com.example.taskku.domain.model.TimetableItem
import com.example.taskku.fakes.FakeSubjectRepository
import com.example.taskku.fakes.FakeTimetableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class TimetableViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var timetableRepository: FakeTimetableRepository
    private lateinit var subjectRepository: FakeSubjectRepository
    private lateinit var viewModel: TimetableViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        timetableRepository = FakeTimetableRepository()
        subjectRepository = FakeSubjectRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun timetableViewModel_loadsAndFiltersBySelectedDay() = runTest(testDispatcher) {
        val itemMonday = TimetableItem(
            id = 1,
            subject = "Matematika",
            dayOfWeek = 1, // Senin
            startTime = "07:30",
            endTime = "09:00",
            room = "10A",
            teacher = "Pak Budi"
        )
        val itemTuesday = TimetableItem(
            id = 2,
            subject = "Fisika",
            dayOfWeek = 2, // Selasa
            startTime = "09:15",
            endTime = "10:45",
            room = "Lab Fisika",
            teacher = "Bu Siti"
        )
        timetableRepository.timetablesFlow.value = listOf(itemMonday, itemTuesday)

        viewModel = TimetableViewModel(timetableRepository, subjectRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        viewModel.onSelectDay(1)
        testScheduler.advanceUntilIdle()
        val mondayList = viewModel.uiState.value.currentDayTimetables
        assertEquals(1, mondayList.size)
        assertEquals("Matematika", mondayList[0].subject)

        viewModel.onSelectDay(2)
        testScheduler.advanceUntilIdle()
        val tuesdayList = viewModel.uiState.value.currentDayTimetables
        assertEquals(1, tuesdayList.size)
        assertEquals("Fisika", tuesdayList[0].subject)

        viewModel.onSelectDay(3) // Rabu (empty)
        testScheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.currentDayTimetables.isEmpty())
    }

    @Test
    fun timetableViewModel_saveAndDeleteItem() = runTest(testDispatcher) {
        viewModel = TimetableViewModel(timetableRepository, subjectRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()

        // Insert new slot
        viewModel.saveTimetable(
            id = 0,
            subject = "Biologi",
            dayOfWeek = 4,
            startTime = "08:00",
            endTime = "09:30",
            room = "Lab Bio",
            teacher = "Pak Joko"
        )
        testScheduler.advanceUntilIdle()

        val allItems = timetableRepository.timetablesFlow.value
        assertEquals(1, allItems.size)
        assertEquals("Biologi", allItems[0].subject)
        val insertedId = allItems[0].id

        // Update existing slot
        viewModel.saveTimetable(
            id = insertedId,
            subject = "Biologi Lanjutan",
            dayOfWeek = 4,
            startTime = "08:00",
            endTime = "10:00",
            room = "Lab Bio 2",
            teacher = "Pak Joko"
        )
        testScheduler.advanceUntilIdle()

        val updatedItems = timetableRepository.timetablesFlow.value
        assertEquals(1, updatedItems.size)
        assertEquals("Biologi Lanjutan", updatedItems[0].subject)
        assertEquals("10:00", updatedItems[0].endTime)

        // Delete slot
        viewModel.deleteTimetable(insertedId)
        testScheduler.advanceUntilIdle()

        assertTrue(timetableRepository.timetablesFlow.value.isEmpty())
    }

    @Test
    fun calculateNextMeetingDate_producesFutureDateWithMatchingTime() {
        val item = TimetableItem(
            id = 1,
            subject = "Kimia",
            dayOfWeek = 3, // Rabu
            startTime = "08:45",
            endTime = "10:15",
            room = "Lab Kimia"
        )

        val nextMeetingMillis = calculateNextMeetingDate(item)
        val now = System.currentTimeMillis()

        assertTrue("Next meeting date must be in the future", nextMeetingMillis > now)

        val cal = Calendar.getInstance().apply { timeInMillis = nextMeetingMillis }
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(45, cal.get(Calendar.MINUTE))
        assertEquals(Calendar.WEDNESDAY, cal.get(Calendar.DAY_OF_WEEK))
    }

    @Test
    fun calculateNextMeetingDate_sameDay_schedulesForNextWeekExactSevenDays() {
        // Reference: Wednesday at 10:00 (day 3)
        val ref = java.time.ZonedDateTime.of(2026, 9, 16, 10, 0, 0, 0, java.time.ZoneId.systemDefault()) // Wednesday
        val item = TimetableItem(
            id = 1,
            subject = "Kimia",
            dayOfWeek = 3, // Rabu
            startTime = "08:45",
            endTime = "10:15"
        )
        val nextMeetingMillis = calculateNextMeetingDate(item, ref)
        val result = java.time.Instant.ofEpochMilli(nextMeetingMillis).atZone(ref.zone)
        assertEquals(java.time.DayOfWeek.WEDNESDAY, result.dayOfWeek)
        assertEquals(23, result.dayOfMonth) // 16 + 7 = 23
        assertEquals(8, result.hour)
        assertEquals(45, result.minute)
    }

    @Test
    fun calculateNextMeetingDate_classEarlierInTheWeek_schedulesForUpcomingMeetingNotTwoWeeksOut() {
        // Reference: Friday at 14:00 (day 5)
        // Class was Wednesday (day 3)
        // Previous bug added 12 days (two weeks away). Fix schedules next Wednesday (5 days away).
        val ref = java.time.ZonedDateTime.of(2026, 9, 18, 14, 0, 0, 0, java.time.ZoneId.systemDefault()) // Friday
        val item = TimetableItem(
            id = 1,
            subject = "Kimia",
            dayOfWeek = 3, // Rabu
            startTime = "08:45",
            endTime = "10:15"
        )
        val nextMeetingMillis = calculateNextMeetingDate(item, ref)
        val result = java.time.Instant.ofEpochMilli(nextMeetingMillis).atZone(ref.zone)
        assertEquals(java.time.DayOfWeek.WEDNESDAY, result.dayOfWeek)
        assertEquals(23, result.dayOfMonth) // Friday 18 + 5 days = Wednesday 23
        assertEquals(8, result.hour)
        assertEquals(45, result.minute)
    }

    @Test
    fun calculateNextMeetingDate_classLaterInTheWeek_schedulesForNextWeeksMeeting() {
        // Reference: Monday at 08:00 (day 1)
        // Class is Thursday (day 4)
        // Next week's Thursday meeting is in 3 + 7 = 10 days
        val ref = java.time.ZonedDateTime.of(2026, 9, 14, 8, 0, 0, 0, java.time.ZoneId.systemDefault()) // Monday
        val item = TimetableItem(
            id = 1,
            subject = "Fisika",
            dayOfWeek = 4, // Kamis
            startTime = "10:00",
            endTime = "11:30"
        )
        val nextMeetingMillis = calculateNextMeetingDate(item, ref)
        val result = java.time.Instant.ofEpochMilli(nextMeetingMillis).atZone(ref.zone)
        assertEquals(java.time.DayOfWeek.THURSDAY, result.dayOfWeek)
        assertEquals(24, result.dayOfMonth) // 14 + 10 = 24
        assertEquals(10, result.hour)
        assertEquals(0, result.minute)
    }

    @Test
    fun calculateNextMeetingDate_onSundayForMonday_schedulesForTomorrow() {
        // Reference: Sunday at 19:00 (day 7)
        // Class is Monday (day 1)
        // Next meeting is tomorrow Monday (1 day away), NOT 8 days away!
        val ref = java.time.ZonedDateTime.of(2026, 9, 20, 19, 0, 0, 0, java.time.ZoneId.systemDefault()) // Sunday
        val item = TimetableItem(
            id = 1,
            subject = "Matematika",
            dayOfWeek = 1, // Senin
            startTime = "07:30",
            endTime = "09:00"
        )
        val nextMeetingMillis = calculateNextMeetingDate(item, ref)
        val result = java.time.Instant.ofEpochMilli(nextMeetingMillis).atZone(ref.zone)
        assertEquals(java.time.DayOfWeek.MONDAY, result.dayOfWeek)
        assertEquals(21, result.dayOfMonth) // 20 + 1 = 21
        assertEquals(7, result.hour)
        assertEquals(30, result.minute)
    }
}
