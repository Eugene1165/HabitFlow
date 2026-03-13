package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KCalendarScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KCalendarScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenCalendar = child<KNode> { hasTestTag("screen_calendar") }
}
