package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.AllHabitsStatistics
import com.example.habitflow.domain.model.HabitResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllHabitsStatisticsUseCase @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getHabitsStatisticsUseCase: GetHabitsStatisticsUseCase
) {

    operator fun invoke(): Flow<HabitResult<AllHabitsStatistics?>> =
        getAllActiveHabitsUseCase.invoke()
            .flatMapLatest { habitsResult ->
                when (habitsResult) {
                    // Нет привычек — нечего агрегировать, ViewModel покажет Empty
                    is HabitResult.Error -> flowOf(
                        HabitResult.Error(
                            habitsResult.exception,
                            habitsResult.message
                        )
                    )

                    is HabitResult.Success -> {
                        if (habitsResult.data.isEmpty()) return@flatMapLatest flowOf(
                            HabitResult.Success(
                                null
                            )
                        )

                        val statsFlow = habitsResult.data.map { habit ->
                            getHabitsStatisticsUseCase(habit.id).map { stats -> habit to stats }
                        }
                        // Для каждой привычки получаем статистики → List<Pair<Habit, HabitStatistics>>
                        combine(statsFlow) { statsArray ->
                            val statsWithHabits = statsArray.toList()
                            // Лидер по лучшему streak за всё время
                            val bestStreak = statsWithHabits
                                .maxByOrNull { it.second.bestStreak }
                                ?.let { it.first to it.second.bestStreak }
                                ?: return@combine HabitResult.Success(null)
                            // Лидер по текущему streak
                            val currentStreak = statsWithHabits
                                .maxByOrNull { it.second.currentStreak }
                                ?.let { it.first to it.second.currentStreak }
                                ?: return@combine HabitResult.Success(null)
                            // Лидер по проценту выполнения
                            val mostConsistent = statsWithHabits
                                .maxByOrNull { it.second.percentCompletion }
                                ?.let { it.first to it.second.percentCompletion }
                                ?: return@combine HabitResult.Success(null)
                            HabitResult.Success(
                                AllHabitsStatistics(
                                    bestStreak,
                                    currentStreak,
                                    mostConsistent,
                                    habitsResult.data.size
                                )
                            )
                        }
                    }
                }
            }

}
