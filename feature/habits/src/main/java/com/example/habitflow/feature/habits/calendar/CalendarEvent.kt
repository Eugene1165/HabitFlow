package com.example.habitflow.feature.habits.calendar

sealed class CalendarEvent {
    object NavigateBack: CalendarEvent()

    data class ShowError(val message: String): CalendarEvent()
}
