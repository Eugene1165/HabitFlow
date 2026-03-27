package com.example.habitflow.feature.habits.archived

sealed class ArchivedEvent {
    data class ShowError(val message: String): ArchivedEvent()
}
