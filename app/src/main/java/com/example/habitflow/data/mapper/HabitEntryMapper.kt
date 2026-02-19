package com.example.habitflow.data.mapper

import com.example.habitflow.data.local.entity.HabitEntryEntity
import com.example.habitflow.domain.model.HabitEntry
import java.time.LocalDate

class HabitEntryMapper {

    fun mapHabitEntryEntityToHabitEntry(
        entity: HabitEntryEntity
    ): HabitEntry {
        return HabitEntry(
            id = entity.id,
            habitId = entity.habitId,
            date = LocalDate.parse(entity.date),
            isDone = entity.isDone
        )
    }

    fun mapHabitEntryToHabitEntryEntity(
        entry: HabitEntry
    ): HabitEntryEntity {
        return HabitEntryEntity(
            id = entry.id,
            habitId = entry.habitId,
            date = entry.date.toString(),
            isDone = entry.isDone
        )
    }
}