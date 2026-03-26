package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(habit: Habit): HabitResult<Habit> {
       return when(val result = repository.addHabit(habit)){
           is HabitResult.Success -> {
               scheduler.schedule(result.data)
               result
           }
           is HabitResult.Error -> {
               result
           }
       }
    }
}
