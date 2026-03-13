package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import java.time.DayOfWeek

class KSettingsScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KSettingsScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenSettings = child<KNode> { hasTestTag("screen_settings") }

    fun switcherTheme(): KNode{
        return child<KNode> {hasTestTag("switcher_theme")}
    }
    fun dayChip(day: DayOfWeek): KNode{
        return child<KNode>{ hasTestTag("day_${day}")}
    }

}
