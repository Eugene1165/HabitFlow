package com.example.habitflow.fakeRepository

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHabitRepository : HabitRepository {
    private val habits = MutableStateFlow<List<Habit>>(emptyList())

    override fun getAllActiveHabits(): Flow<HabitResult<List<Habit>>> =
       habits.map { list -> HabitResult.Success(list.filter { !it.isArchived }) }

    override fun getArchivedHabits(): Flow<List<Habit>> =
        habits.map { list -> list.filter { it.isArchived } }


    override suspend fun getHabitById(habitId: Int): Habit? {
        return habits.value.find { it.id == habitId }
    }

    override fun observeHabitById(habitId: Int): Flow<Habit?> {
        return habits.map { list -> list.find { it.id == habitId } }
    }


    override suspend fun addHabit(habit: Habit): HabitResult<Habit> {
        habits.value += habit
        return HabitResult.Success(habit)
    }

    override suspend fun updateHabit(habit: Habit): HabitResult<Unit> {
        habits.value = habits.value.map {
            if(it.id ==habit.id) habit else it
        }
        return HabitResult.Success(Unit)
    }

    override suspend fun deleteHabit(habitId: Int): HabitResult<Unit> {
        habits.value = habits.value.filter { it.id != habitId }
        return HabitResult.Success(Unit)
    }


    override suspend fun archiveHabit(habitId: Int):HabitResult<Unit> {
        habits.value = habits.value.map {
            if (it.id == habitId) it.copy(isArchived = true) else it
        }
        return HabitResult.Success(Unit)
    }

    override suspend fun restoreHabit(habitId: Int):HabitResult<Unit> {
        habits.value = habits.value.map {
            if (it.id == habitId) it.copy(isArchived = false) else it
        }
        return HabitResult.Success(Unit)
    }
}
