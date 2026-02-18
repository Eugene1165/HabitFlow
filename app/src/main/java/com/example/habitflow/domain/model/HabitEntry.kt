package com.example.habitflow.domain.model

import java.time.LocalDate

//по сути это список наших привычек.То есть создается одна привычка,а HabitEntry это много штук
//этой сущности.То есть есть habit-тренировка,а есть habitEntry(habitId=1,id=1)
//habitEntry(habitId=1,id=2) и тд.то ессть это нужно для списка привычек
data class HabitEntry(
    val id: Int,
    val habitId: Int,
    val date: LocalDate,
    val isDone: Boolean
)
