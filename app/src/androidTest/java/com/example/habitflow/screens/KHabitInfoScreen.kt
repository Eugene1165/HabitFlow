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
    val archiveButton = child<KNode> { hasTestTag("btn_archive_habit") }
    val calendarButton = child<KNode> { hasTestTag("btn_open_calendar") }

    fun habitByTitle(title: String): KNode {
        return child<KNode> { hasText(title) }
    }
}