package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.AllHabitsStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllHabitsStatisticsUseCase @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getHabitsStatisticsUseCase: GetHabitsStatisticsUseCase

) {
    operator fun invoke(): Flow<AllHabitsStatistics?> {
        return getAllActiveHabitsUseCase.invoke()
            .map { habits ->
                // Нет привычек — нечего агрегировать, ViewModel покажет Empty
                if (habits.isEmpty()) return@map null
                // Для каждой привычки получаем снимок статистики → List<Pair<Habit, HabitStatistics>>
                val statsWithHabits =
                    habits.map { habit -> habit to getHabitsStatisticsUseCase(habit.id).first() }
                // Лидер по лучшему streak за всё время
                val bestStreak = statsWithHabits
                    .maxByOrNull { it.second.bestStreak }
                    ?.let { it.first to it.second.bestStreak } ?: return@map null
                // Лидер по текущему streak
                val currentStreak = statsWithHabits
                    .maxByOrNull { it.second.currentStreak }
                    ?.let { it.first to it.second.currentStreak } ?: return@map null
                // Лидер по проценту выполнения
                val mostConsistent = statsWithHabits
                    .maxByOrNull { it.second.percentCompletion }
                    ?.let { it.first to it.second.percentCompletion } ?: return@map null
                AllHabitsStatistics(
                    bestStreak,
                    currentStreak,
                    mostConsistent,
                    habits.size
                )
            }
    }

}
