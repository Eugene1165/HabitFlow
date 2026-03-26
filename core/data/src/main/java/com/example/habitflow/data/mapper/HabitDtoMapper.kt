package com.example.habitflow.data.mapper

import com.example.habitflow.data.remote.dto.HabitDto
import com.example.habitflow.domain.extensions.toRepeatType
import com.example.habitflow.domain.extensions.toTypeString
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class HabitDtoMapper @Inject constructor() {

    fun mapDtoToHabit(dto: HabitDto): Habit {
        return Habit(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate.let { LocalDate.parse(it) },
            color = dto.color,
            target = dto.target,
            isArchived = dto.isArchived,
            repeatType = dto.repeatType.toRepeatType(dto.repeatDays, dto.repeatCount),
            reminder = dto.reminder?.let { LocalTime.parse(it) }
        )

    }

    fun mapHabitToDto(habit: Habit): HabitDto {
        val repeatDays: String? = when (val rt = habit.repeatType) {
            is RepeatType.WeeklyDays -> rt.days.joinToString(",")
            else -> null
        }
        val repeatCount: Int? = when (val rt = habit.repeatType) {
            is RepeatType.WeeklyCount -> rt.count
            else -> null
        }
        return HabitDto(
            id = habit.id,
            title = habit.title,
            description = habit.description,
            startDate = habit.startDate.toString(),
            color = habit.color,
            target = habit.target,
            isArchived = habit.isArchived,
            repeatType = habit.repeatType.toTypeString(),
            repeatDays = repeatDays,
            repeatCount = repeatCount,
            reminder = habit.reminder?.toString()
        )
    }
}
