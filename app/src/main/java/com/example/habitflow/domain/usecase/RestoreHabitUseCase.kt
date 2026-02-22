package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository
import javax.inject.Inject

class RestoreHabitUseCase@Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int){
        return repository.restoreHabit(habitId)
    }
}