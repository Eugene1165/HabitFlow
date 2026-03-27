package com.example.habitflow

import app.cash.turbine.test
import com.example.habitflow.domain.model.AllHabitsStatistics
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.GetAllHabitsStatisticsUseCase
import com.example.habitflow.feature.statistics.StatisticsUiState
import com.example.habitflow.feature.statistics.StatisticsViewModel
import com.example.habitflow.fixtures.HabitFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val useCase = mockk<GetAllHabitsStatisticsUseCase>()
    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when useCase returns success with data then state is Content`() = runTest {
        val habit = HabitFactory.createHabitDomain()
        val data = AllHabitsStatistics(
            bestStreak = Pair(habit, 5),
            currentStreak = Pair(habit, 5),
            mostConsistent = Pair(habit, 0.5f),
            activeHabitsCount = 5
        )

        every { useCase() } returns flowOf(HabitResult.Success(data))
        viewModel = StatisticsViewModel(useCase)

        viewModel.state.test {
            assertEquals(StatisticsUiState.Content(data), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when useCase returns success and  then state is Empty`() = runTest {

        every { useCase() } returns flowOf(HabitResult.Success(null))
        viewModel = StatisticsViewModel(useCase)

        viewModel.state.test {
            assertEquals(StatisticsUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when useCase returns error and then state is Error`() = runTest {

        every { useCase() } returns flowOf(
            HabitResult.Error(
                exception = Exception("error"),
                message = "test"
            )
        )
        viewModel = StatisticsViewModel(useCase)

        viewModel.state.test {
            assertEquals(StatisticsUiState.Error("test"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when useCase throw exeption and then state is Error`() = runTest {

        every { useCase() } returns flow { throw IllegalStateException("Ошибка загрузки") }
        viewModel = StatisticsViewModel(useCase)

        viewModel.state.test {
            assertEquals(StatisticsUiState.Error("Ошибка загрузки"), awaitItem())
        }

    }


}
