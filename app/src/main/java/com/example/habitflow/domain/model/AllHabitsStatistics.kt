package com.example.habitflow.domain.model

data class AllHabitsStatistics(
    val bestStreak: Pair<Habit, Int>,
    val currentStreak: Pair<Habit, Int>,
    val mostConsistent: Pair<Habit, Float>,
    val activeHabitsCount: Int
)