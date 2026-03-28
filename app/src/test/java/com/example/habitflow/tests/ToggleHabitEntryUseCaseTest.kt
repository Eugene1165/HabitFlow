package com.example.habitflow.tests

import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

class ToggleHabitEntryUseCaseTest {
    private val habitEntryRepository = mockk<HabitEntryRepository>()
    private val useCase = ToggleHabitEntryUseCase(habitEntryRepository)

    @Test(expected = IllegalArgumentException::class)
    fun `invoking with future date throws exception`() = runTest {
        val today = LocalDate.of(2026, 3, 2)
        val futureDate = today.plusDays(1)

        useCase(habitId = 1, date = futureDate, today = today)
    }

    @Test
    fun `existingEntry equals null `() = runTest {
        val date = LocalDate.of(2026, 1, 2)

        coEvery { habitEntryRepository.getEntryByDate(1, date) } returns null
        coEvery { habitEntryRepository.addEntry(any()) } just Runs

        useCase(habitId = 1, date = date)

        coVerify {
            habitEntryRepository.addEntry(
                match {
                    it.isDone && it.habitId == 1 && it.date == date
                }
            )
        }
    }

    @Test
    fun `existingEntry have entry and isdone equal true`() = runTest {
        val date = LocalDate.of(2026, 1, 2)
        val entry = HabitEntry(isDone = true, id = 1, habitId = 1, date = date)

        coEvery { habitEntryRepository.getEntryByDate(any(), any()) } returns entry
        coEvery { habitEntryRepository.updateEntry(any(), any(), any(), any()) } just Runs

        useCase(habitId = 1, date = date)
        coVerify { habitEntryRepository.updateEntry(1, date, isDone = false, any()) }

    }

    @Test
    fun `existingEntry have entry and isdone equal false`() = runTest {
        val date = LocalDate.of(2026, 1, 2)
        val entry = HabitEntry(isDone = false, id = 1, habitId = 1, date = date)

        coEvery { habitEntryRepository.getEntryByDate(any(), any()) } returns entry
        coEvery { habitEntryRepository.updateEntry(any(), any(), any(), any()) } just Runs

        useCase(habitId = 1, date = date)
        coVerify { habitEntryRepository.updateEntry(1, date, isDone = true, any()) }

    }
}
