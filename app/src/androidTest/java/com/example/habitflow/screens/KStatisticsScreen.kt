package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KStatisticsScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KStatisticsScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenStatistics = child<KNode> { hasTestTag("screen_statistics") }
    val activeHabitCard = child<KNode> { hasTestTag("active_habit_card") }
    val streakHabitCard = child<KNode> { hasTestTag("streak_habit_card") }
    val mostHabitCard = child<KNode> { hasTestTag("most_habit_card") }


}
