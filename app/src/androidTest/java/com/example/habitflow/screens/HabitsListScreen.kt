package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HabitsListScreen (
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<HabitsListScreen>(
    semanticsProvider = semanticsProvider
) {

    val habitsList = child<KNode> { hasTestTag("habits_list") }
    val fabAddHabit = child<KNode> { hasTestTag("fab_add_habit") }

    fun checkboxForHabit(habitId: Int): KNode {
       return child<KNode> { hasTestTag("checkbox_habit_${habitId}") }
    }
}