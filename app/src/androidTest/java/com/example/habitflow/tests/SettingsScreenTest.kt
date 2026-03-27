package com.example.habitflow.tests

import com.example.habitflow.BaseAllureTestCase


import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KMainScreen
import com.example.habitflow.screens.KSettingsScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
@Epic("HabitFlow")
@Feature("SettingsScreen")
@HiltAndroidTest
class SettingsScreenTest: BaseAllureTestCase() {

    @get:Rule(order=0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp(){
        onComposeScreen<KMainScreen>(composeTestRule){
            navigateToSettings()
        }
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    fun settingsDisplayed() = run{
        step("Проверяем что экран отображается"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                screenSettings.assertIsDisplayed()
            }
        }
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    fun toggleDarkTheme() = run{
        step("Проверяем что свич неактивный(белая тема"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                switcherTheme().assertIsOff()
            }
        }
        step("переключаем тему на темную"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                switcherTheme().performClick()
            }
        }
        step("Проверяем что свич активный(темная тема"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                switcherTheme().assertIsOn()
            }
        }
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test
    fun changeFirstDayOfWeek() = run{
        step("проверяем что выбран monday(default"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                dayChip(DayOfWeek.MONDAY).assertIsSelected()
            }
        }
        step("Выбираем первый день привычки"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                dayChip(DayOfWeek.WEDNESDAY).performClick()
            }
        }
        step("проверяем что день активный и выбрался"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                dayChip(DayOfWeek.WEDNESDAY).assertIsSelected()
            }
        }
    }
}
