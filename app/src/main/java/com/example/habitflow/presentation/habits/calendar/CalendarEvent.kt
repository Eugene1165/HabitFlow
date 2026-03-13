package com.example.habitflow.presentation.habits.calendar

sealed class CalendarEvent {
    object NavigateBack: CalendarEvent()

    data class ShowError(val message: String): CalendarEvent()
}
