package com.example.habitflow.feature.habits.list

import com.example.habitflow.domain.model.Habit

data class HabitWithStatus(val habit: Habit, val isCompletedToday: Boolean)
