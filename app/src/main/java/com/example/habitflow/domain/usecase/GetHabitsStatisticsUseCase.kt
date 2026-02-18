package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.model.HabitStatistics
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetHabitsStatisticsUseCase(
    private val habitRepository: HabitRepository,
    private val habitEntryRepository: HabitEntryRepository
) {
    operator fun invoke(
        habitId: Int,
    ): Flow<HabitStatistics> = flow {
        val habit = habitRepository.getHabitById(habitId) ?: return@flow
        val result = habitEntryRepository.getEntriesForPeriod(
            habitId, habit.startDate,
            LocalDate.now()
        )
        result.collect { entries ->
            emit(calculateStatistics(habit, entries))
        }

    }

    private fun calculateStatistics(habit: Habit, entries: List<HabitEntry>): HabitStatistics {
        val completedDays = entries.count { it.isDone }
        val activeDays = entries.size
        if (activeDays == 0) return HabitStatistics(0,0,0)
        val completionPercent = completedDays.toFloat() / activeDays
        return HabitStatistics(
            currentStreak = 0,
            bestStreak = 0,
            percentCompletion = completionPercent
        )

    }
}


