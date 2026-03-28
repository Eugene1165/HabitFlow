package com.example.habitflow.tests

import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KArchivedScreen
import com.example.habitflow.screens.KHabitsListScreen
import com.example.habitflow.screens.KMainScreen
import com.example.habitflow.screens.KSettingsScreen
import com.example.habitflow.screens.KStatisticsScreen
import com.example.habitflow.HabitBaseTestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest : HabitBaseTestCase() {

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
    fun navigateToSettingsScreen() = run {
        step("Тапаем на таб settings") {
            onComposeScreen<KMainScreen>(composeTestRule) {
                navigateToSettings()
            }

        }
        step("Проверяем что открылся ") {
            onComposeScreen<KSettingsScreen>(composeTestRule) {
                screenSettings { assertIsDisplayed() }
            }
        }
    }

    @Test
    fun navigateToStatisticsScreen() = run {
        step("Тапаем на таб statistics") {
            onComposeScreen<KMainScreen>(composeTestRule) {
                navigateToStatistics()
            }

        }
        step("Проверяем что открылся ") {
            onComposeScreen<KStatisticsScreen>(composeTestRule) {
                screenStatistics { assertIsDisplayed() }
            }
        }
    }

    @Test
    fun navigateToArchivedScreen() = run {
        step("Тапаем на таб archived") {
            onComposeScreen<KMainScreen>(composeTestRule) {
                navigateToArchived()
            }

        }
        step("Проверяем что открылся ") {
            onComposeScreen<KArchivedScreen>(composeTestRule) {
                screenArchived { assertIsDisplayed() }
            }
        }
    }

    @Test
    fun navigateToHabitsListScreen() = run {
        step("переходим на экран статистики"){
            onComposeScreen<KMainScreen>(composeTestRule) {
                navigateToSettings()
            }

        }
        step("Тапаем на таб Habits") {
            onComposeScreen<KMainScreen>(composeTestRule) {
                navigateToHabitsList()
            }

        }
        step("Проверяем что открылся ") {
            onComposeScreen<KHabitsListScreen>(composeTestRule) {
                screenHabitsList { assertIsDisplayed() }
            }
        }
    }
}
