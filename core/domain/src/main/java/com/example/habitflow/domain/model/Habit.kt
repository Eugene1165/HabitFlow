package com.example.habitflow.domain.model

import java.time.LocalTime
import java.time.LocalDate

data class Habit(
    val id: Int,
    val title: String,
    val description: String? = null,
    val startDate: LocalDate,
    val color: String,
    val target: Int?,
    val isArchived: Boolean = false,
    val repeatType: RepeatType,
    val reminder: LocalTime?
)
