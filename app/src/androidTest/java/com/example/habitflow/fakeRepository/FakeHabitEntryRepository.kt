package com.example.habitflow.fakeRepository

import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class FakeHabitEntryRepository : HabitEntryRepository {
    private val entrys = MutableStateFlow<List<HabitEntry>>(emptyList())
    override suspend fun addEntry(entry: HabitEntry) {
        entrys.value += entry
    }

    override fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>> {
        return entrys.map { list -> list.filter { entry -> entry.habitId == habitId } }
    }

    override fun getEntriesForPeriod(
        habitId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HabitEntry>> {
        return entrys.map { list ->
            list.filter { entry ->
                entry.habitId == habitId && (entry.date >= startDate && entry.date <= endDate)
            }
        }
    }

    override fun getEntriesForDate(date: LocalDate): Flow<List<HabitEntry>> {
        return entrys.map { list -> list.filter { entry -> entry.date == date } }
    }

    override suspend fun updateEntry(
        habitId: Int,
        currentDate: LocalDate,
        isDone: Boolean
    ) {
        entrys.value = entrys.value.map {
            if (it.habitId == habitId && it.date == currentDate) it.copy(isDone = isDone) else it
        }
    }

    override suspend fun getEntryByDate(
        habitId: Int,
        date: LocalDate
    ): HabitEntry? {
        return entrys.value.find { entry -> entry.habitId == habitId && entry.date == date }
    }
}
