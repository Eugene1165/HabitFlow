package com.example.habitflow.data.repository

import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.mapper.HabitEntryMapper
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class HabitEntryRepositoryImpl @Inject constructor(
    private val dao: HabitEntryDao,
    private val habitEntryMapper: HabitEntryMapper,
) : HabitEntryRepository {
    override suspend fun addEntry(entry: HabitEntry) {
        dao.addEntry(habitEntryMapper.mapHabitEntryToHabitEntryEntity(entry))
    }

    override fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>> {
        return dao.getEntriesForHabit(habitId)
            .map { list -> list.map { habitEntryMapper.mapHabitEntryEntityToHabitEntry(it) } }
    }

    override fun getEntriesForPeriod(
        habitId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HabitEntry>> {
        return dao.getEntriesForPeriod(habitId, startDate.toString(), endDate.toString())
            .map { list -> list.map { habitEntryMapper.mapHabitEntryEntityToHabitEntry(it) } }
    }

    override fun getEntriesForDate(date: LocalDate): Flow<List<HabitEntry>> {
        return dao.getEntriesForDate(date.toString())
            .map { list -> list.map { habitEntryMapper.mapHabitEntryEntityToHabitEntry(it) } }
    }

    override suspend fun updateEntry(
        habitId: Int,
        currentDate: LocalDate,
        isDone: Boolean
    ) {
        return dao.updateEntry(habitId, currentDate.toString(), isDone)
    }

    override suspend fun getEntryByDate(
        habitId: Int,
        date: LocalDate
    ): HabitEntry? {
        return dao.getEntryByDate(habitId, date.toString())?.let {
            habitEntryMapper.mapHabitEntryEntityToHabitEntry(it)
        }
    }
}