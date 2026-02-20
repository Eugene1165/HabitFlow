package com.example.habitflow.presentation.habits

import com.example.habitflow.domain.model.Habit

data class HabitWithStatus(val habit: Habit, val isCompletedToday: Boolean)
