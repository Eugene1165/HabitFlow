package com.example.habitflow.domain.model
import java.time.DayOfWeek

sealed class RepeatType {
    object Daily: RepeatType()
    data class WeeklyDays(val days:List<DayOfWeek>): RepeatType()
    data class WeeklyCount(val count:Int): RepeatType()
}