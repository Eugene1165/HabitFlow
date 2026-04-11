package com.example.habitflow.fixtures

import com.example.habitflow.data.local.entity.HabitEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
@Suppress("LongParameterList")
object HabitEntityFactory {
    fun createHabitEntity(
        id: Int = 1,
        title: String = "test",
        color: String = "#F44336",
        target: Int? = 0,
        reminder: String? = LocalTime.of(8, 0).toString(),
        repeatType: String = "DAILY",
        startDate: String = LocalDate.of(2026, 3, 2).toString(),
        description: String? = null,
        isArchived: Boolean = false,
        repeatDays: String? = DayOfWeek.WEDNESDAY.toString(),
        repeatCount: Int? = 0
    ) = HabitEntity(
        id,
        title,
        description,
        startDate,
        color,
        isArchived,
        repeatType,
        repeatDays,
        repeatCount,
        reminder,
        target,
    )
}
