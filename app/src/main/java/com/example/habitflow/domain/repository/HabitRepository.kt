package com.example.habitflow.domain.repository

import com.example.habitflow.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    //показать список всех привычек.
    fun getAllActiveHabits(): Flow<List<Habit>>

    fun getArchivedHabits(): Flow<List<Habit>>
    suspend fun getHabitById(habitId: Int): Habit?
    fun observeHabitById(habitId: Int): Flow<Habit?>
    suspend fun addHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Int)

    suspend fun archiveHabit(habitId: Int)

    suspend fun restoreHabit(habitId: Int)
}