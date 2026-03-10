package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.screens.KHabitFormScreen
import com.example.habitflow.screens.KHabitInfoScreen
import com.example.habitflow.screens.KHabitsListScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
class HabitsListTest : TestCase() {
    @Inject
    lateinit var fakeHabitRepository: HabitRepository

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
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun habitsListInDisplayed() = run {
        step("Проверяем что fab отображается") {
            onComposeScreen<KHabitsListScreen>(composeTestRule) {
                fabAddHabit {
                    assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun fabNavigate() = run {
        step("Тапаем на FAB") {
            onComposeScreen<KHabitsListScreen>(composeTestRule) {
                fabAddHabit {
                    performClick()
                }
            }
        }
        step("Проверяем что открылся экран создания привычки") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                screenHabitForm {
                    assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun toggleHabitCheckbox() = run{
        step("проверяем что чекбокс выключен"){
            onComposeScreen<KHabitsListScreen>(composeTestRule){
                checkboxForHabit(1).assertIsOff()
            }
        }
        step("Кликаем на чекбокс"){
            onComposeScreen<KHabitsListScreen>(composeTestRule){
                checkboxForHabit(1).performClick()
            }
        }
        step("проверяем что чекбокс включен"){
            onComposeScreen<KHabitsListScreen>(composeTestRule){
                checkboxForHabit(1).assertIsOn()
            }
        }
    }

    @Test
    fun navigateToHabitInfo()= run{
        step("кликаем по карточке"){
            onComposeScreen<KHabitsListScreen>(composeTestRule){
                habitCardById(1).performClick()
            }
        }
        step("проверяем что находимся на экране привычки"){
            onComposeScreen<KHabitInfoScreen>(composeTestRule){
                screenHabitInfo.assertIsDisplayed()
                habitByTitle("Бег").assertIsDisplayed()
            }
        }
    }
}