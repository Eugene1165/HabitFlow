package com.example.habitflow.domain.repository

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    //показать список всех привычек.
    fun getAllActiveHabits(): Flow<HabitResult<List<Habit>>>

    fun getArchivedHabits(): Flow<List<Habit>>
    suspend fun getHabitById(habitId: Int): Habit?
    fun observeHabitById(habitId: Int): Flow<Habit?>
    suspend fun addHabit(habit: Habit): HabitResult<Habit>
    suspend fun updateHabit(habit: Habit) : HabitResult<Unit>
    suspend fun deleteHabit(habitId: Int) : HabitResult<Unit>

    suspend fun archiveHabit(habitId: Int) : HabitResult<Unit>

    suspend fun restoreHabit(habitId: Int) : HabitResult<Unit>
}
