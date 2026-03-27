package com.example.habitflow.tests

import app.cash.turbine.test
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.usecase.GetHabitsStatisticsUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime


class GetHabitStatisticsUseCaseTest {
    private val habitRepository = mockk<HabitRepository>()
    private val habitEntryRepository = mockk<HabitEntryRepository>()
    private val useCase = GetHabitsStatisticsUseCase(habitRepository, habitEntryRepository)


    @Test
    fun `daily habit all days completed returns full streak and 100 percent`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val startDate = today.minusDays(2)
        val habit = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.Daily,
            startDate = startDate,
        )
        val entriesOne = HabitEntry(
            id = 1, habitId = 1, date = today, isDone = true
        )
        val entriesTwo = HabitEntry(
            id = 2, habitId = 1, date = today.minusDays(1), isDone = true
        )
        val entriesThree = HabitEntry(
            id = 3, habitId = 1, date = today.minusDays(2), isDone = true
        )

        every { habitRepository.observeHabitById(1) } returns flowOf(habit)
        every {
            habitEntryRepository.getEntriesForPeriod(
                habitId = 1,
                startDate = startDate,
                endDate = today
            )
        } returns flowOf(listOf(entriesOne, entriesTwo, entriesThree))

        val result = useCase(habitId = 1, today = today)
        result.test {
            val stats = awaitItem()
            assertEquals(3, stats.currentStreak)
            assertEquals(3, stats.bestStreak)
            assertEquals(1.0f, stats.percentCompletion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `daily habit with gap returns streak 1 and 40 percent`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val startDate = today.minusDays(4)
        val habit = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.Daily,
            startDate = startDate,
        )
        val entriesOne = HabitEntry(
            id = 1, habitId = 1, date = today, isDone = true
        )
        val entriesTwo = HabitEntry(
            id = 2, habitId = 1, date = today.minusDays(1), isDone = false
        )
        val entriesThree = HabitEntry(
            id = 3, habitId = 1, date = today.minusDays(2), isDone = true
        )

        every { habitRepository.observeHabitById(1) } returns flowOf(habit)
        every {
            habitEntryRepository.getEntriesForPeriod(
                habitId = 1,
                startDate = startDate,
                endDate = today
            )
        } returns flowOf(listOf(entriesOne, entriesTwo, entriesThree))

        val result = useCase(habitId = 1, today = today)
        result.test {
            val stats = awaitItem()
            assertEquals(1, stats.currentStreak)
            assertEquals(1, stats.bestStreak)
            assertEquals(0.4f, stats.percentCompletion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weeklyDays one entries skip`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val startDate = today.minusDays(14)
        val days = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val habit = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.WeeklyDays(days),
            startDate = startDate,
        )
        val entriesOne = HabitEntry(
            id = 1, habitId = 1, date = LocalDate.of(2026,2,16), isDone = true
        )
        val entriesTwo = HabitEntry(
            id = 2, habitId = 1, date = LocalDate.of(2026,2,18), isDone = true
        )
        val entriesThree = HabitEntry(
            id = 3, habitId = 1, date = LocalDate.of(2026,2,20), isDone = false
        )
        val entriesFour = HabitEntry(
            id = 4, habitId = 1, date = LocalDate.of(2026,2,23), isDone = true
        )

        every { habitRepository.observeHabitById(1) } returns flowOf(habit)
        every {
            habitEntryRepository.getEntriesForPeriod(
                habitId = 1,
                startDate = startDate,
                endDate = today
            )
        } returns flowOf(listOf(entriesOne, entriesTwo, entriesThree,entriesFour))

        val result = useCase(habitId = 1, today = today)
        result.test {
            val stats = awaitItem()
            assertEquals(0, stats.currentStreak)
            assertEquals(2, stats.bestStreak)
            assertEquals(3f/7f, stats.percentCompletion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weeklyCount one day was skipped`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val startDate = today.minusDays(21)
        val habit = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.WeeklyCount(3),
            startDate = startDate,
        )
        val entriesOne = HabitEntry(
            id = 1, habitId = 1, date = LocalDate.of(2026,2,10), isDone = true
        )
        val entriesTwo = HabitEntry(
            id = 2, habitId = 1, date = LocalDate.of(2026,2,12), isDone = true
        )
        val entriesThree = HabitEntry(
            id = 3, habitId = 1, date = LocalDate.of(2026,2,14), isDone = true
        )

        every { habitRepository.observeHabitById(1) } returns flowOf(habit)
        every {
            habitEntryRepository.getEntriesForPeriod(
                habitId = 1,
                startDate = startDate,
                endDate = today
            )
        } returns flowOf(listOf(entriesOne, entriesTwo, entriesThree))

        val result = useCase(habitId = 1, today = today)
        result.test {
            val stats = awaitItem()
            assertEquals(0, stats.currentStreak)
            assertEquals(1, stats.bestStreak)
            assertEquals(0.25f, stats.percentCompletion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weeklyCount one week completed current week empty`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val startDate = today.minusDays(7)
        val habit = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.WeeklyCount(2),
            startDate = startDate,
        )
        val entriesOne = HabitEntry(
            id = 1, habitId = 1, date = LocalDate.of(2026,2,24), isDone = true
        )
        val entriesTwo = HabitEntry(
            id = 2, habitId = 1, date = LocalDate.of(2026,2,26), isDone = true
        )
        val entriesThree = HabitEntry(
            id = 3, habitId = 1, date = LocalDate.of(2026,2,28), isDone = true
        )
        val entriesFour = HabitEntry(
            id = 4, habitId = 1, date = LocalDate.of(2026,3,1), isDone = true
        )

        every { habitRepository.observeHabitById(1) } returns flowOf(habit)
        every {
            habitEntryRepository.getEntriesForPeriod(
                habitId = 1,
                startDate = startDate,
                endDate = today
            )
        } returns flowOf(listOf(entriesOne, entriesTwo, entriesThree,entriesFour))

        val result = useCase(habitId = 1, today = today)
        result.test {
            val stats = awaitItem()
            assertEquals(0, stats.currentStreak)
            assertEquals(1, stats.bestStreak)
            assertEquals(1.0f, stats.percentCompletion)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
