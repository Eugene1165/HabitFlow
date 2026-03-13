package com.example.habitflow.tests


import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KHabitFormScreen
import com.example.habitflow.screens.KHabitsListScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HabitFormTest : TestCase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.waitForIdle()
        onComposeScreen<KHabitsListScreen>(composeTestRule) {
            fabAddHabit { performClick() }
        }
    }

    @Test
    fun createHabitSuccess() = run {
        step("Вводим название") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                titleField { performTextReplacement("test") }
            }
        }
        step("Вводим описание") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                descriptionField { performTextReplacement("test") }
            }
        }
        step("тапаем на кнопку и сохраняем результат") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                saveButton { performClick() }
            }
        }

        step("Ждём закрытия формы") {
            onComposeScreen<KHabitFormScreen>(composeTestRule){
                composeTestRule.waitUntil(timeoutMillis = 10000) {
                    composeTestRule
                        .onAllNodesWithTag("screen_habit_form")
                        .fetchSemanticsNodes()
                        .isEmpty()
                }
            }
        }

        step("Проверяем что привычка отображается в списке") {
            onComposeScreen<KHabitsListScreen>(composeTestRule) {
                habitByTitle("test").assertIsDisplayed()
            }
        }
    }


    @Test
    fun createHabitEmptyTitle() = run {
        step("тапаем на кнопку сохранения") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                saveButton { performClick() }
            }
        }
        step("проверяем что остались на экране формы"){
            onComposeScreen<KHabitFormScreen>(composeTestRule){
                screenHabitForm{assertIsDisplayed()}
            }
        }
        step("проверяем что отображается snackBar"){
            onComposeScreen<KHabitFormScreen>(composeTestRule){
                errorSnackbar.assertIsDisplayed()
            }
        }
    }
}
