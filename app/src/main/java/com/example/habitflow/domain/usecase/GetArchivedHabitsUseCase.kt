package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetArchivedHabitsUseCase(private val repository: HabitRepository) {
    operator fun invoke():Flow<List<Habit>>{
        return repository.getArchivedHabits()
    }
}