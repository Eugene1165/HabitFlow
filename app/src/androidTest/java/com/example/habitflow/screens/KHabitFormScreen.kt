package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KHabitFormScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KHabitFormScreen>(
    semanticsProvider = semanticsProvider
){
    val screenHabitForm = child<KNode> { hasTestTag("screen_habit_form") }
}