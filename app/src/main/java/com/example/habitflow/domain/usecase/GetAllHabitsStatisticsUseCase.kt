package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.AllHabitsStatistics
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAllHabitsStatisticsUseCase @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getHabitsStatisticsUseCase: GetHabitsStatisticsUseCase

) {
    suspend operator fun invoke(): AllHabitsStatistics? {
        // Снимок всех активных привычек из БД через .first()
        val habits = getAllActiveHabitsUseCase.invoke().first()
        // Нет привычек — нечего агрегировать, ViewModel покажет Empty
        if (habits.isEmpty()) return null
        // Для каждой привычки получаем снимок статистики → List<Pair<Habit, HabitStatistics>>
        val statsWithHabits =
            habits.map { habit -> habit to getHabitsStatisticsUseCase(habit.id).first() }
        // Лидер по лучшему streak за всё время
        val bestStreak = statsWithHabits
            .maxByOrNull { it.second.bestStreak }
            ?.let { it.first to it.second.bestStreak } ?: return null
        // Лидер по текущему streak
        val currentStreak = statsWithHabits
            .maxByOrNull { it.second.currentStreak }
            ?.let { it.first to it.second.currentStreak } ?: return null
        // Лидер по проценту выполнения
        val mostConsistent = statsWithHabits
            .maxByOrNull { it.second.percentCompletion }
            ?.let { it.first to it.second.percentCompletion } ?: return null
        return AllHabitsStatistics(bestStreak, currentStreak, mostConsistent, habits.size)
    }

}