package com.example.habitflow.presentation.habits.create

sealed class CreateHabitEvent{
    object NavigateToHabitsList: CreateHabitEvent()
}