package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository

class RestoreHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int){
        return repository.restoreHabit(habitId)
    }
}