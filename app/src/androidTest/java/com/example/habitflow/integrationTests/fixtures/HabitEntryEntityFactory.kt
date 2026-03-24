package com.example.habitflow.integrationTests.fixtures

import com.example.habitflow.data.local.entity.HabitEntryEntity
import java.time.LocalDate

object HabitEntryEntityFactory {
    fun createHabitEntryEntity(
        id: Int = 1,
        habitId: Int = 1,
        date: String = LocalDate.of(2026, 2, 2).toString(),
        isDone: Boolean = false,
        isSynced: Boolean = false,
        updatedAt: String = "1970-01-01T00:00:00"
    ) = HabitEntryEntity(
        id,
        habitId,
        date,
        isDone,
        isSynced,
        updatedAt,
    )
}
