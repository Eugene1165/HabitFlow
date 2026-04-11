package com.example.habitflow.tests

import app.cash.turbine.test
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import com.example.habitflow.domain.usecase.GetEntriesForDateUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import com.example.habitflow.feature.habits.list.HabitWithStatus
import com.example.habitflow.feature.habits.list.HabitsListUiState
import com.example.habitflow.feature.habits.list.HabitsListViewModel
import com.example.habitflow.fixtures.HabitFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HabitsListViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val getAllActiveHabitsUseCase = mockk<GetAllActiveHabitsUseCase>()
    private val getEntriesForDateUseCase = mockk<GetEntriesForDateUseCase>()
    private val toggleHabitEntryUseCase = mockk<ToggleHabitEntryUseCase>()

    private lateinit var viewModel: HabitsListViewModel

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

        every { getAllActiveHabitsUseCase() } returns flowOf(HabitResult.Success(listOf(habit)))
        every { getEntriesForDateUseCase(any()) } returns flowOf(emptyList())

        viewModel = HabitsListViewModel(
            getAllActiveHabitsUseCase,
            getEntriesForDateUseCase,
            toggleHabitEntryUseCase
        )
        viewModel.state.test {
            val expected = HabitsListUiState.Content(
                listOf(HabitWithStatus(habit = habit, isCompletedToday = false))
            )
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when useCase returns success with empty then state is Empty`() = runTest {

        every { getAllActiveHabitsUseCase() } returns flowOf(HabitResult.Success(emptyList()))
        every { getEntriesForDateUseCase(any()) } returns flowOf(emptyList())

        viewModel = HabitsListViewModel(
            getAllActiveHabitsUseCase,
            getEntriesForDateUseCase,
            toggleHabitEntryUseCase
        )
        viewModel.state.test {
            val expected = HabitsListUiState.Empty
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}
