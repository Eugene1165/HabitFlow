package com.example.habitflow.tests


import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.screens.KMainScreen
import com.example.habitflow.screens.KSettingsScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek

@HiltAndroidTest
class SettingsScreenTest: TestCase() {

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

    @Test
    fun settingsDisplayed() = run{
        step("Проверяем что экран отображается"){
            onComposeScreen<KSettingsScreen>(composeTestRule){
                screenSettings.assertIsDisplayed()
            }
        }
    }

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
