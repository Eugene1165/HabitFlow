package com.example.habitflow.data.mapper

import com.example.habitflow.data.local.entity.HabitEntity
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class HabitMapper {
    fun mapHabitEntityToHabit(
        entity: HabitEntity,
        ): Habit {
        val repeatType: RepeatType = when(entity.repeatType){
            "DAILY" -> RepeatType.Daily
            "WEEKLY_DAYS" -> RepeatType.WeeklyDays(entity.repeatDays!!.split(",").map { DayOfWeek.valueOf(it)})
            "WEEKLY_COUNT" -> RepeatType.WeeklyCount(entity.repeatCount!!)
            else -> throw IllegalArgumentException("Unknown repeatType: ${entity.repeatType}")
        }
        return Habit(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            startDate = entity.startDate.let { LocalDate.parse(it) },
            color = entity.color,
            target = entity.target,
            isArchived = entity.isArchived,
            repeatType = repeatType,
            reminder = entity.reminder?.let { LocalTime.parse(it) }
        )
    }

    fun mapHabitToHabitEntity(
        habit: Habit,
    ): HabitEntity {
        val repeatType: String = when (habit.repeatType) {
            is RepeatType.Daily -> "DAILY"
            is RepeatType.WeeklyDays -> "WEEKLY_DAYS"
            is RepeatType.WeeklyCount -> "WEEKLY_COUNT"
        }
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
            repeatType = repeatType,
            repeatDays = repeatDays,
            repeatCount = repeatCount,
            reminder = habit.reminder?.toString(),
            target = habit.target
        )
    }
}