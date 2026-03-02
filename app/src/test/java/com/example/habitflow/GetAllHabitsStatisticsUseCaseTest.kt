package com.example.habitflow

import app.cash.turbine.test
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitStatistics
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import com.example.habitflow.domain.usecase.GetAllHabitsStatisticsUseCase
import com.example.habitflow.domain.usecase.GetHabitsStatisticsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetAllHabitsStatisticsUseCaseTest {
    private val getAllActiveHabitsUseCase = mockk<GetAllActiveHabitsUseCase>()
    private val getHabitsStatisticsUseCase = mockk<GetHabitsStatisticsUseCase>()
    private val useCase = GetAllHabitsStatisticsUseCase(
        getAllActiveHabitsUseCase,
        getHabitsStatisticsUseCase
    )

    @Test
    fun `two habits with different statistics`() = runTest {
        val habitOne = Habit(
            id = 1,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.Daily,
            startDate = LocalDate.of(2026, 3, 2),
        )
        val habitTwo = Habit(
            id = 2,
            title = "Бег",
            color = "",
            target = 0,
            reminder = LocalTime.of(1, 2),
            repeatType = RepeatType.Daily,
            startDate = LocalDate.of(2026, 3, 3),
        )

        val stats1 = HabitStatistics(2, 5, 0.9f)
        val stats2 = HabitStatistics(7, 3, 0.6f)

        every { getAllActiveHabitsUseCase() } returns flowOf(listOf(habitOne, habitTwo))
        every { getHabitsStatisticsUseCase(habitOne.id) } returns flowOf(stats1)
        every { getHabitsStatisticsUseCase(habitTwo.id) } returns flowOf(stats2)

        val result = useCase()
        result.test {
            val stats = awaitItem()
            assertEquals(Pair(habitTwo, 7), stats?.currentStreak)
            assertEquals(Pair(habitOne, 5), stats?.bestStreak)
            assertEquals(Pair(habitOne, 0.9f), stats?.mostConsistent)
            assertEquals(2, stats?.activeHabitsCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `usecase returns habits list with empty`() = runTest {
        val emptyList = emptyList<Habit>()

        every { getAllActiveHabitsUseCase() } returns flowOf(emptyList)

        val result = useCase()
        result.test {
            val stats = awaitItem()
            assertEquals(null, stats)
            cancelAndIgnoreRemainingEvents()
        }
    }
}