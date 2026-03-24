package com.example.habitflow.presentation.archived

sealed class ArchivedEvent {
    data class ShowError(val message: String): ArchivedEvent()
}
