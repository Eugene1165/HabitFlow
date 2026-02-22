package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import java.time.LocalDate
import javax.inject.Inject

class ToggleHabitEntryUseCase @Inject constructor(private val repository: HabitEntryRepository) {
    suspend operator fun invoke(habitId: Int, date: LocalDate) {
        if (date.isAfter(LocalDate.now()))
            throw IllegalArgumentException(
                "Пользователь НЕ может отметить выполнение в будущем "
            )
        val existingEntry = repository.getEntryByDate(habitId, date)
        if (existingEntry == null) {
            val newEntry = HabitEntry(id = 0, habitId = habitId, date = date, isDone = true)
            repository.addEntry(newEntry)
        } else repository.updateEntry(habitId, date, !existingEntry.isDone)
    }
}