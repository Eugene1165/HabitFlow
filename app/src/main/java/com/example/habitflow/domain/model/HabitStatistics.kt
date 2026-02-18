package com.example.habitflow.domain.model

data class HabitStatistics(
    val currentStreak: Int,
    val bestStreak: Int,
    val percentCompletion : Float,
)