package com.example.habitflow.domain.repository

import com.example.habitflow.domain.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitEntryRepository {

    suspend fun addEntry(entry: HabitEntry)

    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>>

    fun getEntriesForPeriod(
        habitId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HabitEntry>>

    fun getEntriesForDate(date: LocalDate): Flow<List<HabitEntry>>

    suspend fun updateEntry(habitId: Int, currentDate: LocalDate, isDone: Boolean)

    suspend fun getEntryByDate(habitId: Int,date: LocalDate): HabitEntry?
}