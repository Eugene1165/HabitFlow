package com.example.habitflow

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.AddHabitUseCase
import com.example.habitflow.domain.usecase.GetHabitByIdUseCase
import com.example.habitflow.domain.usecase.UpdateHabitUseCase
import com.example.habitflow.fixtures.HabitFactory
import com.example.habitflow.presentation.habits.form.HabitFormEvent
import com.example.habitflow.presentation.habits.form.HabitFormViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HabitFormViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val addHabitUseCase = mockk<AddHabitUseCase>()
    private val getHabitByIdUseCase = mockk<GetHabitByIdUseCase>()
    private val updateHabitUseCase = mockk<UpdateHabitUseCase>()
    private lateinit var viewModel: HabitFormViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSave empty title and throw error`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("habitId" to -1))

        viewModel = HabitFormViewModel(
            savedStateHandle = savedStateHandle,
            addHabitUseCase = addHabitUseCase,
            getHabitByIdUseCase = getHabitByIdUseCase,
            updateHabitUseCase = updateHabitUseCase
        )

        viewModel.onTitleChanged("")
        viewModel.onSave()
        assertEquals("Заполните название привычки", viewModel.state.value.error)
        coVerify(exactly = 0) { addHabitUseCase(any()) }

    }

    @Test
    fun `onSave with valid title creates habit and navigates to list`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("habitId" to -1))
        val habit = HabitFactory.createHabitDomain()

        viewModel = HabitFormViewModel(
            savedStateHandle = savedStateHandle,
            addHabitUseCase = addHabitUseCase,
            getHabitByIdUseCase = getHabitByIdUseCase,
            updateHabitUseCase = updateHabitUseCase
        )

        coEvery { addHabitUseCase(any()) } returns HabitResult.Success(habit)
        viewModel.onTitleChanged("test")

        viewModel.events.test {
            viewModel.onSave()
            assertEquals(HabitFormEvent.NavigateToHabitsList, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { addHabitUseCase(any()) }
    }

    @Test
    fun `onSave with valid title updates habit and navigates to habit info`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("habitId" to 1))
        val habit = HabitFactory.createHabitDomain()

        coEvery { getHabitByIdUseCase(1) } returns habit
        coEvery { updateHabitUseCase(any()) } returns HabitResult.Success(Unit)

        viewModel = HabitFormViewModel(
            savedStateHandle = savedStateHandle,
            addHabitUseCase = addHabitUseCase,
            getHabitByIdUseCase = getHabitByIdUseCase,
            updateHabitUseCase = updateHabitUseCase
        )

        viewModel.events.test {
            viewModel.onSave()
            assertEquals(HabitFormEvent.NavigateToHabitFormInfo(habit.id), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { updateHabitUseCase(any()) }
    }

    @Test
    fun `onSave with valid title when addHabit fails then state has error`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("habitId" to -1))

        coEvery { addHabitUseCase(any()) } returns HabitResult.Error(
            Exception("error"),
            message = "test"
        )

        viewModel = HabitFormViewModel(
            savedStateHandle = savedStateHandle,
            addHabitUseCase = addHabitUseCase,
            getHabitByIdUseCase = getHabitByIdUseCase,
            updateHabitUseCase = updateHabitUseCase
        )

        viewModel.onTitleChanged("test")
        viewModel.onSave()
        assertEquals("test", viewModel.state.value.error)
        assertEquals(viewModel.state.value.isSaving, false)
    }
}
