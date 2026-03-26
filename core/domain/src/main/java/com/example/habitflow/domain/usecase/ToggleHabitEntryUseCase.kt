package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class ToggleHabitEntryUseCase @Inject constructor(private val repository: HabitEntryRepository) {
    suspend operator fun invoke(habitId: Int, date: LocalDate, today: LocalDate = LocalDate.now()) {
        require(!date.isAfter(today)) {
            "Нельзя отметить дату будущего"
        }
        val existingEntry = repository.getEntryByDate(habitId, date)
        if (existingEntry == null) {
            val newEntry = HabitEntry(
                id = 0,
                habitId = habitId,
                date = date,
                isDone = true,
                updatedAt = LocalDateTime.now()
            )
            repository.addEntry(newEntry)
        } else repository.updateEntry(
            habitId,
            date,
            !existingEntry.isDone,
            updatedAt = LocalDateTime.now()
        )
    }
}
