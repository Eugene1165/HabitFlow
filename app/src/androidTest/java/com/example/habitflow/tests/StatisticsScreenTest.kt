package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.screens.KBottomNav
import com.example.habitflow.screens.KStatisticsScreen
import com.example.habitflow.HabitBaseTestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
@HiltAndroidTest
class StatisticsScreenTest: HabitBaseTestCase() {

    @Inject
    lateinit var fakeHabitRepository: HabitRepository

    @Inject
    lateinit var fakeHabitEntryRepository: HabitEntryRepository

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            fakeHabitRepository.addHabit(
                Habit(
                    id = 1,
                    title = "Бег",
                    color = "#F44336",
                    target = 0,
                    reminder = LocalTime.of(1, 2),
                    repeatType = RepeatType.Daily,
                    startDate = LocalDate.of(2026, 3, 2),
                    description = null,
                    isArchived = false
                )
            )
            fakeHabitEntryRepository.addEntry(HabitEntry(
                id=2,
                habitId = 1,
                date = LocalDate.now(),
                isDone = true
            ))
        }
        onComposeScreen<KBottomNav>(composeTestRule){
            navigateToStatistics()
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun statisticsWithHabits()=run{
        step("Проверяем что экран отображается"){
            onComposeScreen<KStatisticsScreen>(composeTestRule){
                screenStatistics.assertIsDisplayed()
            }
        }
        step("Проверяем что отображается блок активных привычек"){
            onComposeScreen<KStatisticsScreen>(composeTestRule){
                activeHabitCard.assertIsDisplayed()
            }
        }
        step("Проверяем что отображается блок серии привычек"){
            onComposeScreen<KStatisticsScreen>(composeTestRule){
                streakHabitCard.assertIsDisplayed()
            }
        }
        step("Проверяем что отображается блок процента привычек"){
            onComposeScreen<KStatisticsScreen>(composeTestRule){
                mostHabitCard.assertIsDisplayed()
            }
        }
    }
}
