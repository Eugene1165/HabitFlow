package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import javax.inject.Inject

class RestoreHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: Int): HabitResult<Unit> {
        return when (val result = repository.restoreHabit(habitId)) {
            is HabitResult.Success -> result
            is HabitResult.Error -> result
        }
    }
}
