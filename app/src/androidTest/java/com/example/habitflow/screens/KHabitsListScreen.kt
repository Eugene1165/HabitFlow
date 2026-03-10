package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KHabitsListScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KHabitsListScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenHabitsList = child<KNode> { hasTestTag("habit_list_screen") }

    val habitsList = child<KNode> { hasTestTag("habits_list") }
    val fabAddHabit = child<KNode> { hasTestTag("fab_add_new_habit") }

    fun checkboxForHabit(habitId: Int): KNode {
        return child<KNode> { hasTestTag("checkbox_habit_${habitId}") }
    }

    fun habitByTitle(title: String): KNode {
        return child<KNode> { hasText(title) }
    }

    fun habitCardById(habitId: Int): KNode {
        return child<KNode> { hasTestTag("habit_item_id_$habitId") }
    }
}