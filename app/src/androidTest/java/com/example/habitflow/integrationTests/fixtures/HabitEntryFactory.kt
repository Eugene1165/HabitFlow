package com.example.habitflow.integrationTests.fixtures

import com.example.habitflow.domain.model.HabitEntry
import java.time.LocalDate
import java.time.LocalDateTime


@Suppress("LongParameterList")
object HabitEntryFactory {
    fun createHabitEntryDomain(
        id: Int = 1,
        habitId: Int = 1,
        date: LocalDate = LocalDate.of(2026, 3, 2),
        isDone: Boolean = false,
        updatedAt: LocalDateTime = LocalDateTime.parse("1970-01-01T00:00:00")
    ) = HabitEntry(
        id,
        habitId,
        date,
        isDone,
        updatedAt,
    )
}
