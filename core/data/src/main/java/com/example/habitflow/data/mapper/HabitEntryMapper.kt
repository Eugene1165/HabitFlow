package com.example.habitflow.data.mapper

import com.example.habitflow.data.local.entity.HabitEntryEntity
import com.example.habitflow.domain.model.HabitEntry
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class HabitEntryMapper @Inject constructor() {

    fun mapHabitEntryEntityToHabitEntry(
        entity: HabitEntryEntity
    ): HabitEntry {
        return HabitEntry(
            id = entity.id,
            habitId = entity.habitId,
            date = LocalDate.parse(entity.date),
            isDone = entity.isDone,
            updatedAt = LocalDateTime.parse(entity.updatedAt)
        )
    }

    fun mapHabitEntryToHabitEntryEntity(
        entry: HabitEntry,
        isSynced: Boolean = false
    ): HabitEntryEntity {
        return HabitEntryEntity(
            id = entry.id,
            habitId = entry.habitId,
            date = entry.date.toString(),
            isDone = entry.isDone,
            isSynced = isSynced,
            updatedAt = entry.updatedAt.toString()
        )
    }
}
