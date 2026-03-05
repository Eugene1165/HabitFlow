package com.example.habitflow.data.mapper

import com.example.habitflow.data.remote.dto.HabitEntryDto
import com.example.habitflow.domain.model.HabitEntry
import java.time.LocalDate
import javax.inject.Inject

class HabitEntryDtoMapper @Inject constructor() {

    fun mapHabitEntryToDto(entry: HabitEntry): HabitEntryDto {
        return HabitEntryDto(
            id = entry.id,
            habitId = entry.habitId,
            date = entry.date.toString(),
            isDone = entry.isDone
        )
    }

    fun mapDtoToHabitEntry(dto: HabitEntryDto): HabitEntry {
        return HabitEntry(
            id = dto.id,
            habitId = dto.habitId,
            date = dto.date.let { LocalDate.parse(it) },
            isDone = dto.isDone
        )
    }
}