package com.example.habitflow.feature.habits.info

sealed class HabitInfoEvent {
    object NavigateBack : HabitInfoEvent()
    data class NavigateToCalendar(val habitId: Int) : HabitInfoEvent()
}
