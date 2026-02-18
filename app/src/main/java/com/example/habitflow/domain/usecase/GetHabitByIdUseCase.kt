package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository

class GetHabitByIdUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int): Habit?{
        return repository.getHabitById(habitId)
    }
}