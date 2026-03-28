package com.example.habitflow.tests


import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import com.example.habitflow.MainActivity
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.screens.KCalendarScreen
import com.example.habitflow.screens.KHabitInfoScreen
import com.example.habitflow.screens.KHabitsListScreen
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
class HabitInfoTest: HabitBaseTestCase() {
    @Inject
    lateinit var fakeHabitRepository: HabitRepository

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp(){
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
        }
        onComposeScreen<KHabitsListScreen>(composeTestRule){
            habitCardById(1).performClick()
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun habitInfoDisplayed() = run{
        step("Проверяем что отображается информация на экране"){
            onComposeScreen<KHabitInfoScreen>(composeTestRule){
                screenHabitInfo.assertIsDisplayed()
            }
        }
    }

    @Test
    fun archiveHabit() = run{
        step("Архивируем привычку"){
            onComposeScreen<KHabitInfoScreen>(composeTestRule){
                archiveButton.performClick()
            }
        }
        step("Проверяем что привычки нет в списке"){
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("Бег")
                    .fetchSemanticsNodes()
                    .isEmpty()
            }

        }
    }

    @Test
    fun navigateToCalendar() = run{
        step("тапаем на кнопку перехода в календарь"){
            onComposeScreen<KHabitInfoScreen>(composeTestRule){
                calendarButton.performClick()
            }
        }

        step("проверяем что перешли в календарь"){
            onComposeScreen<KCalendarScreen>(composeTestRule){
                screenCalendar.assertIsDisplayed()
            }
        }
    }
}
