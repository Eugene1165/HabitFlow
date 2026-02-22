package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArchivedHabitsUseCase @Inject constructor(private val repository: HabitRepository) {
    operator fun invoke(): Flow<List<Habit>> {
        return repository.getArchivedHabits()
    }
}