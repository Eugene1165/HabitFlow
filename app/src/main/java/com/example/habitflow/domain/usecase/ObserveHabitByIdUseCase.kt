package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(habitId: Int): Flow<Habit?> {
        return repository.observeHabitById(habitId)
    }
}