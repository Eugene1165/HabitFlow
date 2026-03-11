package com.example.habitflow.tests

import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.screens.KArchivedScreen
import com.example.habitflow.screens.KMainScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
class ArchivedScreenTest : TestCase(){

    @Inject
    lateinit var fakeHabitRepository: HabitRepository

    @get:Rule(order=0)
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
                    isArchived = true
                )
            )
        }
        onComposeScreen<KMainScreen>(composeTestRule){
            navigateToArchived()
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun archivedListDisplayed() = run{
        step("Проверяем что привычка в списке отображается"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                habitCardById(1).assertIsDisplayed()
            }
        }
    }

    @Test
    fun restoreHabit() = run{
        step("Восстановим привычку из архива"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                restoreBtn(1).performClick()
            }
        }
        step("проверим что в списке не осталось привычки"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                habitCardById(1).assertDoesNotExist()
            }
        }
    }

    @Test
    fun deleteHabit() = run{
        step("Удалим привычку из архива"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                deleteBtn(1).performClick()
            }
        }
        step("подветрждаем удаление в диалоге"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                confirmDeleteBtn.performClick()
            }
        }
        step("проверим что в списке не осталось привычки"){
            onComposeScreen<KArchivedScreen>(composeTestRule){
                habitCardById(1).assertDoesNotExist()
            }
        }
    }
}