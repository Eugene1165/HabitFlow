package com.example.habitflow.fakeRepository

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHabitRepository : HabitRepository {
    private val habits = MutableStateFlow<List<Habit>>(emptyList())

    override fun getAllActiveHabits(): Flow<List<Habit>> =
        habits.map { list -> list.filter { !it.isArchived } }

    override fun getArchivedHabits(): Flow<List<Habit>> =
        habits.map { list -> list.filter { it.isArchived } }


    override suspend fun getHabitById(habitId: Int): Habit? {
        return habits.value.find { it.id == habitId }
    }

    override fun observeHabitById(habitId: Int): Flow<Habit?> {
        return habits.map { list -> list.find { it.id == habitId } }
    }


    override suspend fun addHabit(habit: Habit): Habit {
        habits.value += habit
        return habit
    }

    override suspend fun updateHabit(habit: Habit) {
        habits.value = habits.value.map {
            if(it.id ==habit.id) habit else it
        }
    }

    override suspend fun deleteHabit(habitId: Int) {
        habits.value = habits.value.filter { it.id != habitId }
    }


    override suspend fun archiveHabit(habitId: Int) {
        habits.value = habits.value.map {
            if (it.id == habitId) it.copy(isArchived = true) else it
        }
    }

    override suspend fun restoreHabit(habitId: Int) {
        habits.value = habits.value.map {
            if (it.id == habitId) it.copy(isArchived = false) else it
        }
    }
}
