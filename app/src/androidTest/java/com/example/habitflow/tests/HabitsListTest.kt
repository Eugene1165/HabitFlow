package com.example.habitflow.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.habitflow.MainActivity
import com.example.habitflow.domain.usecase.CompleteOnBoardingUseCase
import com.example.habitflow.screens.HabitsListScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HabitsListTest : TestCase() {

    @Inject
    lateinit var completeOnBoardingUseCase: CompleteOnBoardingUseCase

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp(){
        hiltRule.inject()
        runBlocking { completeOnBoardingUseCase }
        composeTestRule.waitForIdle()
    }

    @Test
    fun habitsListInDisplayed() = run {
        ComposeScreen.onComposeScreen<HabitsListScreen>(composeTestRule){
            fabAddHabit{
                assertIsDisplayed()
            }
        }
    }

}