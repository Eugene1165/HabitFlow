package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KHabitInfoScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KHabitInfoScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenHabitInfo = child<KNode> { hasTestTag("screen_habit_info") }

}