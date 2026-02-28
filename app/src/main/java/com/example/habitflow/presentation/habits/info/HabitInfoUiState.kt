package com.example.habitflow.presentation.habits.info

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.model.HabitStatistics

sealed class HabitInfoUiState() {
    object Loading : HabitInfoUiState()
    data class Content(
        val habit: Habit,
        val statistics: HabitStatistics,
        val isTodayDone: Boolean,
        val weeklyEntries: List<HabitEntry>,
    ) : HabitInfoUiState()
    data class Error(val message: String) : HabitInfoUiState()
}