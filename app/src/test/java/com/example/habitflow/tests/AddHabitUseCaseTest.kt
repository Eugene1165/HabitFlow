package com.example.habitflow.tests

import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import com.example.habitflow.domain.usecase.AddHabitUseCase
import com.example.habitflow.fixtures.HabitFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddHabitUseCaseTest {
    private val habitRepository = mockk<HabitRepository>()
    private val scheduler = mockk<ReminderScheduler>()
    private val useCase = AddHabitUseCase(habitRepository, scheduler)

    @Test
    fun `Result success and scheduler start`() = runTest {
        val habit = HabitFactory.createHabitDomain()

        coEvery { habitRepository.addHabit(habit) } returns HabitResult.Success(HabitFactory.createHabitDomain())
        every { scheduler.schedule(habit)} just Runs

        useCase(habit)

        verify { scheduler.schedule(habit) }
    }

    @Test
    fun `Result error and scheduler not start`() = runTest {
        val habit = HabitFactory.createHabitDomain()

        coEvery { habitRepository.addHabit(habit) } returns HabitResult.Error(Exception(),"")
        every { scheduler.schedule(habit)} just Runs

        useCase(habit)

        verify(exactly = 0) { scheduler.schedule(habit) }
    }
}
