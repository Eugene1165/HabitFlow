package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int): Habit? {
        return repository.getHabitById(habitId)
    }
}