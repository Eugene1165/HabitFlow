package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class ArchiveHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(habitId: Int) {
        repository.archiveHabit(habitId)
        scheduler.cancel(habitId)
    }
}
