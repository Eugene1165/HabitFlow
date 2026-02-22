package com.example.habitflow.domain.usecase


import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.model.HabitStatistics
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class GetHabitsStatisticsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val habitEntryRepository: HabitEntryRepository
) {

    operator fun invoke(
        habitId: Int,
    ): Flow<HabitStatistics> {
        return habitRepository.observeHabitById(habitId)
            .flatMapLatest { habit ->
                if (habit == null) return@flatMapLatest emptyFlow()
                habitEntryRepository.getEntriesForPeriod(
                    habitId = habitId,
                    startDate = habit.startDate,
                    endDate = LocalDate.now()
                ).map { entries ->
                    calculateStatistics(habit, entries)
                }
            }
    }


    private fun calculateStatistics(habit: Habit, entries: List<HabitEntry>): HabitStatistics {
        val completedDays = entries.count { it.isDone }
        val activeDays = when (val repeatType = habit.repeatType) {
            is RepeatType.Daily -> {
                ChronoUnit.DAYS.between(
                    habit.startDate,
                    LocalDate.now()
                ).toInt()+1
            }

            is RepeatType.WeeklyDays -> {
                var count = 0
                var date = habit.startDate
                while (!date.isAfter(LocalDate.now())) {
                    if (date.dayOfWeek in repeatType.days) count++
                    date = date.plusDays(1)
                }
                count
            }

            is RepeatType.WeeklyCount -> {
                (ChronoUnit.WEEKS.between(
                    habit.startDate,
                    LocalDate.now()).toInt() + 1) * repeatType.count
            }
        }
        if (activeDays == 0) return HabitStatistics(0, 0, 0f)
        val completionPercent = completedDays.toFloat() / activeDays

        val currentStreak = when (val repeatType = habit.repeatType) {
            is RepeatType.Daily -> calculateDailyStreak(entries)
            is RepeatType.WeeklyDays -> calculateWeeklyDaysStreak(entries, repeatType.days)
            is RepeatType.WeeklyCount -> calculateWeeklyCountStreak(entries, repeatType.count)
        }

        val bestStreak = when (val repeatType = habit.repeatType) {
            is RepeatType.Daily -> calculateDailyBestStreak(entries)
            is RepeatType.WeeklyDays -> calculateWeeklyDaysBestStreak(entries, repeatType.days)
            is RepeatType.WeeklyCount -> calculateWeeklyCountBestStreak(entries, repeatType.count)
        }
        return HabitStatistics(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            percentCompletion = completionPercent
        )
    }

    private fun calculateDailyStreak(entries: List<HabitEntry>): Int {
        val completedDates = entries
            .filter { it.isDone }
            .map { it.date }
            .toSet()
        var streak = 0

        var checkDate = LocalDate.now()

        while (completedDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private fun calculateWeeklyDaysStreak(entries: List<HabitEntry>, days: List<DayOfWeek>): Int {
        val completedDates = entries
            .filter { it.isDone }
            .map { it.date }
            .toSet()
        var streak = 0
        var date = LocalDate.now()

        while (true) {
            if (date.dayOfWeek !in days) {
                date = date.minusDays(1)
                continue
            }

            if (completedDates.contains(date)) {
                streak++
                date = date.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateWeeklyCountStreak(entries: List<HabitEntry>, requiredCount: Int): Int {
        val byWeek = entries.groupBy {
            it.date.get(WeekFields.ISO.weekOfWeekBasedYear())
        }

        var streak = 0
        var currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfWeekBasedYear())

        while (true) {
            val weekEntries = byWeek[currentWeek] ?: break
            val completedCount = weekEntries.count { it.isDone }

            if (completedCount >= requiredCount) {
                streak++
                currentWeek--
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateDailyBestStreak(entries: List<HabitEntry>): Int {
        val completedDates = entries
            .filter { it.isDone }
            .map { it.date }
            .toSet()

        var best = 0
        var current = 0
        var date = entries.minOfOrNull { it.date } ?: return 0

        while (!date.isAfter(LocalDate.now())) {
            if (completedDates.contains(date)) {
                current++
                if (current > best) best = current
            } else {
                current = 0
            }
            date = date.plusDays(1)
        }
        return best
    }

    private fun calculateWeeklyDaysBestStreak(
        entries: List<HabitEntry>,
        days: List<DayOfWeek>
    ): Int {
        val completedDates = entries
            .filter { it.isDone }
            .map { it.date }
            .toSet()
        var best = 0
        var current = 0
        var date = entries.minOfOrNull { it.date } ?: return 0 //начальная дата

        while (!date.isAfter(LocalDate.now())) {
            if (date.dayOfWeek in days) {
                if (completedDates.contains(date)) {
                    current++
                    if (current > best) best = current
                } else current = 0
            }
            date = date.plusDays(1)
        }
        return best
    }

    private fun calculateWeeklyCountBestStreak(
        entries: List<HabitEntry>,
        requiredCount: Int
    ): Int {
        val byWeek = entries.groupBy {
            it.date.get(WeekFields.ISO.weekOfWeekBasedYear())
        }

        var best = 0
        var current = 0

        val sortedWeeks = byWeek.entries.sortedBy { it.key }

        for ((_, week) in sortedWeeks) {
            val completedCount = week.count { it.isDone }
            if (completedCount >= requiredCount) {
                current++
                if (current > best) best = current
            } else current = 0
        }
        return best
    }
}


