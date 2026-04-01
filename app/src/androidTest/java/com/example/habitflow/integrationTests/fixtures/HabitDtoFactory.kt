package com.example.habitflow.integrationTests.fixtures

import com.example.habitflow.data.remote.dto.HabitDto
import com.example.habitflow.domain.model.RepeatType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object HabitDtoFactory {
    fun createHabitDto(
        id: Int = 1,
        title: String = "Бег",
        color: String = "#F44336",
        target: Int? = 0,
        reminder: String? = LocalTime.of(1, 2).toString(),
        repeatType: String = RepeatType.Daily.toString(),
        startDate: String = LocalDate.of(2026, 3, 2).toString(),
        description: String? = null,
        isArchived: Boolean = false,
        repeatDays: String? = DayOfWeek.WEDNESDAY.toString(),
        repeatCount: Int? = 0
    ) = HabitDto(
        id,
        title,
        description,
        startDate,
        color,
        target,
        isArchived,
        repeatType,
        repeatDays,
        repeatCount,
        reminder,
    )
}
