package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository

import com.example.habitflow.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class ArchiveHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(habitId: Int): HabitResult<Unit> {
        return when(val result = repository.archiveHabit(habitId)){
            is HabitResult.Success -> {
                scheduler.cancel(habitId)
                result
            }
            is HabitResult.Error -> result
        }
    }
}
