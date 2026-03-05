package com.example.habitflow.data.mapper

import com.example.habitflow.data.local.entity.HabitEntity
import com.example.habitflow.domain.model.Habit
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import com.example.habitflow.domain.extensions.toRepeatType
import com.example.habitflow.domain.extensions.toTypeString
import com.example.habitflow.domain.model.RepeatType

class HabitMapper @Inject constructor() {
    fun mapHabitEntityToHabit(
        entity: HabitEntity,
    ): Habit {
        return Habit(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            startDate = entity.startDate.let { LocalDate.parse(it) },
            color = entity.color,
            target = entity.target,
            isArchived = entity.isArchived,
            repeatType = entity.repeatType.toRepeatType(entity.repeatDays, entity.repeatCount),
            reminder = entity.reminder?.let { LocalTime.parse(it) }
        )
    }

    fun mapHabitToHabitEntity(
        habit: Habit,
    ): HabitEntity {
        val repeatDays: String? = when (val rt = habit.repeatType) {
            is RepeatType.WeeklyDays -> rt.days.joinToString(",")
            else -> null
        }
        val repeatCount: Int? = when (val rt = habit.repeatType) {
            is RepeatType.WeeklyCount -> rt.count
            else -> null
        }
        return HabitEntity(
            id = habit.id,
            title = habit.title,
            description = habit.description,
            startDate = habit.startDate.toString(),
            color = habit.color,
            isArchived = habit.isArchived,
            repeatType = habit.repeatType.toTypeString(),
            repeatDays = repeatDays,
            repeatCount = repeatCount,
            reminder = habit.reminder?.toString(),
            target = habit.target
        )
    }
}