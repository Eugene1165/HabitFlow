package com.example.habitflow.presentation.habits.info

sealed class HabitInfoEvent {
    object NavigateBack : HabitInfoEvent()
    data class NavigateToCalendar(val habitId: Int) : HabitInfoEvent()
}