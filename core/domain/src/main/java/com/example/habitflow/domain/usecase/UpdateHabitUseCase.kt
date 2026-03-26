package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(habit: Habit):HabitResult<Unit> {
       return when (val result =repository.updateHabit(habit)) {
            is HabitResult.Success -> {
                scheduler.schedule(habit)
                result
            }
            is HabitResult.Error -> {
                result
            }
        }
    }
}
