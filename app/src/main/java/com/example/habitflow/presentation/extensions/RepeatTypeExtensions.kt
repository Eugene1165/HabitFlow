package com.example.habitflow.presentation.extensions

import com.example.habitflow.domain.model.RepeatType
import java.time.DayOfWeek


fun RepeatType.toDisplayName(): String = when (this) {
    is RepeatType.Daily -> "Ежедневно"
    is RepeatType.WeeklyDays -> "По дням недели"
    is RepeatType.WeeklyCount -> "Раз в неделю"
}

fun DayOfWeek.toDisplayName(): String = when (this) {
    DayOfWeek.MONDAY -> "Пн"
    DayOfWeek.TUESDAY -> "Вт"
    DayOfWeek.WEDNESDAY -> "Cр"
    DayOfWeek.THURSDAY -> "Чт"
    DayOfWeek.FRIDAY -> "Пт"
    DayOfWeek.SUNDAY -> "Вс"
    DayOfWeek.SATURDAY -> "Сб"
}

