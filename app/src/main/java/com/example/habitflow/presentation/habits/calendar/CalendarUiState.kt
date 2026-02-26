package com.example.habitflow.presentation.habits.calendar

import com.example.habitflow.domain.model.Habit
import java.time.LocalDate
import java.time.YearMonth

sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Content(
        val habit: Habit,
        val yearMonth: YearMonth,
        val doneDates: Set<LocalDate>
    ) : CalendarUiState()

    data class Error(val message: String) : CalendarUiState()
}