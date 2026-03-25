package com.example.habitflow.fixtures

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalTime
import kotlin.Int
@Suppress("LongParameterList")
object HabitFactory {
    fun createHabitDomain(
        id: Int = 1,
        title: String = "test",
        description: String? = null,
        startDate: LocalDate = LocalDate.of(2026, 3, 2),
        color: String = "#F44336",
        target: Int? = 0,
        isArchived: Boolean = false,
        repeatType: RepeatType = RepeatType.Daily,
        reminder: LocalTime? = LocalTime.of(8, 0)
    ) = Habit(
        id,
        title,
        description,
        startDate,
        color,
        target,
        isArchived,
        repeatType,
        reminder,
    )


}
