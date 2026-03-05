package com.example.habitflow.domain.extensions

import com.example.habitflow.domain.model.RepeatType
import java.lang.IllegalArgumentException
import java.time.DayOfWeek


fun RepeatType.toTypeString(): String {
    return when (this) {
        is RepeatType.Daily -> "DAILY"
        is RepeatType.WeeklyDays -> "WEEKLY_DAYS"
        is RepeatType.WeeklyCount -> "WEEKLY_COUNT"
    }
}


fun String.toRepeatType(repeatDays: String?, repeatCount: Int?): RepeatType {
    return when (this) {
        "DAILY" -> RepeatType.Daily
        "WEEKLY_DAYS" -> repeatDays?.let { days ->
            RepeatType.WeeklyDays(days.split(",").map { DayOfWeek.valueOf(it) })
        } ?: throw IllegalArgumentException("repeatDays in null for WEEKLY_DAYS")

        "WEEKLY_COUNT" -> repeatCount?.let { repeatCount -> RepeatType.WeeklyCount(repeatCount) }
            ?: throw IllegalArgumentException("repeatCount in null for WEEKLY_COUNT")

        else -> throw IllegalArgumentException("Unknown repeatType: $this")
    }

}


