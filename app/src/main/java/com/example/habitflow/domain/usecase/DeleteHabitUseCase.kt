package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository

class DeleteHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int){
        return repository.deleteHabit(habitId)
    }
}