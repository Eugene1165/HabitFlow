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
    val confirmDeleteBtn = child<KNode> { hasText("Удалить") }


    fun restoreBtn(habitId: Int): KNode {
        return child<KNode> { hasTestTag("restore_btn_${habitId}") }
    }

    fun deleteBtn(habitId: Int): KNode {
        return child<KNode> { hasTestTag("delete_btn_${habitId}") }
    }

    fun habitCardById(habitId: Int): KNode {
        return child<KNode> { hasTestTag("habit_item_id_$habitId") }
    }

}