package com.example.habitflow.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KArchivedScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<KArchivedScreen>(
    semanticsProvider = semanticsProvider
) {
    val screenArchived = child<KNode> { hasTestTag("screen_archived") }

}