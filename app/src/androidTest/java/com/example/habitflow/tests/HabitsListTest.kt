package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KHabitFormScreen
import com.example.habitflow.screens.KHabitsListScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HabitsListTest : TestCase() {
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

}