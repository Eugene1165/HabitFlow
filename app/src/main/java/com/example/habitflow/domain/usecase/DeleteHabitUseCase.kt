package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(habitId: Int) {
        repository.deleteHabit(habitId)
        scheduler.cancel(habitId)
    }
}
