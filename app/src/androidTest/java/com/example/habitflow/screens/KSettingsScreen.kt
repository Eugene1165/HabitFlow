package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KSettingsScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KSettingsScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenSettings = child<KNode> { hasTestTag("screen_settings") }

}