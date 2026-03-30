package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.habitflow.HabitBaseTestCase
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KHabitFormScreen
import com.example.habitflow.screens.KHabitsListScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HabitFormTest : HabitBaseTestCase() {
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

    @After
    fun tearDown(){
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .setOrientationNatural()
    }

    @Test
    fun createHabitSuccess() = run {
        step("Вводим название") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                inputText(titleField,"test")
            }
        }
        step("Вводим описание") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                inputText(descriptionField,"test2")
            }
        }
        step("тапаем на кнопку и сохраняем результат") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                saveButton { performClick() }
            }
        }

        step("Ждём закрытия формы") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
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
        step("проверяем что остались на экране формы") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                screenHabitForm { assertIsDisplayed() }
            }
        }
        step("проверяем что отображается snackBar") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                errorSnackbar.assertIsDisplayed()
            }
        }
    }

    @Test
    fun titleSavedAfterRotation() = run {
        step("Вводим название привычки") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                inputText(titleField,"test")
            }
        }

        step("переворачиваем экран") {
            device.exploit.rotate()
        }

        step("проверяем что название сохранилось") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                titleField { assertTextContains("test") }
            }
        }
    }

    @Test
    fun habitFormStatePreservedOnCall() = run {
        step("Вводим название привычки") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                inputText(titleField,"test")
            }
        }
        step("звонок") {
            device.phone.emulateCall("1234567890")
        }
        step("отклоняем звонок") {
            device.phone.cancelCall("1234567890")
        }
        step("проверяем что остались на экране формы") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                screenHabitForm { assertIsDisplayed() }
            }
        }
        step("проверяем что название сохранилось") {
            onComposeScreen<KHabitFormScreen>(composeTestRule) {
                titleField { assertTextContains("test") }
            }
        }
    }
}
