package com.example.habitflow.presentation.habits.create

import com.example.habitflow.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalTime

data class CreateHabitUiState(
    val title: String = "",
    val description: String = "",
    val color: String = "#FF5733",
    val repeatType: RepeatType = RepeatType.Daily,
    val startDate: LocalDate = LocalDate.now(),
    val target: Int? = null,
    val reminder: LocalTime? = null,
    val isSaving: Boolean = false,   // пока идёт сохранение
    val error: String? = null
)