package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.habitflow.HabitBaseTestCase
import com.example.habitflow.MainActivity
import com.example.habitflow.data.local.workmanager.ReminderWorker
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.integrationTests.fixtures.HabitEntityFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
class NotificationTest : HabitBaseTestCase() {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var fakeHabitRepository: HabitRepository

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.waitForIdle()
    }

    @Test
    fun reminderWorkerShowsNotification() = runTest {
        fakeHabitRepository.addHabit(
            Habit(
                id = 1,
                title = "Бег",
                color = "#F44336",
                target = 0,
                reminder = LocalTime.of(1, 2),
                repeatType = RepeatType.Daily,
                startDate = LocalDate.of(2026, 3, 2),
            )
        )
        val worker = TestListenableWorkerBuilder<ReminderWorker>(
            context = InstrumentationRegistry.getInstrumentation().targetContext
        )
            .setWorkerFactory(workerFactory)
            .setInputData(workDataOf("habit_id" to 1))
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(),result)
    }
}
