package com.example.habitflow.feature.habits.form

sealed class HabitFormEvent {
    object NavigateToHabitsList : HabitFormEvent()
    data class NavigateToHabitFormInfo(val habitId: Int) : HabitFormEvent()
}
