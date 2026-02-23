package com.example.habitflow.presentation.habits.list

import com.example.habitflow.presentation.habits.list.HabitWithStatus

sealed class HabitsListUiState {
    object Loading : HabitsListUiState()
    object Empty : HabitsListUiState()
    data class Content (val habits:List<HabitWithStatus>): HabitsListUiState()
    data class Error (val message: String): HabitsListUiState()
}