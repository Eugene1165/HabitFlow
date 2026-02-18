package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository

class AddHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(habit: Habit){
        return repository.addHabit(habit)
    }
}