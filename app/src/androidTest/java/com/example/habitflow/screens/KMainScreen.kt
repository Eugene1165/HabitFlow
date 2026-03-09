package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KMainScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KMainScreen>(
    semanticsProvider = semanticsProvider
) {
    val tabHabits = child<KNode> { hasTestTag("tab_habits_list") }
    val tabStatistics = child<KNode> { hasTestTag("tab_habits_statistics") }
    val tabSettings = child<KNode> { hasTestTag("tab_settings") }
    val tabArchived = child<KNode> { hasTestTag("tab_archived") }

    fun navigateToHabitsList() {
        tabHabits { performClick() }
    }

    fun navigateToStatistics() {
        tabStatistics { performClick() }
    }

    fun navigateToSettings() {
        tabSettings { performClick() }
    }

    fun navigateToArchived() {
        tabArchived { performClick() }
    }
}