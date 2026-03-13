package com.example.habitflow.presentation.archived

import com.example.habitflow.domain.model.Habit

sealed class ArchivedUiState {
    object Loading: ArchivedUiState()
    object Empty : ArchivedUiState()
    data class Error(val message: String) : ArchivedUiState()
    data class Content(val habits: List<Habit>) : ArchivedUiState()

}
