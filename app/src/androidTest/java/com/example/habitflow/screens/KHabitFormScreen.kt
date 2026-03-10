package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KHabitFormScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KHabitFormScreen>(
    semanticsProvider = semanticsProvider
){
    val errorSnackbar = child<KNode> { hasText("Заполните название привычки") }
    val screenHabitForm = child<KNode> { hasTestTag("screen_habit_form") }
    val titleField = child<KNode> { hasTestTag("habit_name") }
    val descriptionField = child<KNode> { hasTestTag("habit_description") }
    val saveButton = child<KNode> { hasTestTag("btn_save_habit") }
}