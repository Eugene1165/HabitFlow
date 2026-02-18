package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository

class ArchiveHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int){
        return repository.archiveHabit(habitId)
    }
}