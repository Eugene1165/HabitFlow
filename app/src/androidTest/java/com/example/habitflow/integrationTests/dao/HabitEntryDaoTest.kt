package com.example.habitflow.integrationTests.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.local.database.HabitDatabase
import com.example.habitflow.data.local.entity.HabitEntryEntity
import com.example.habitflow.integrationTests.fixtures.HabitEntityFactory
import com.example.habitflow.integrationTests.fixtures.HabitEntryEntityFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class HabitEntryDaoTest {
    private lateinit var habitDatabase: HabitDatabase
    private lateinit var habitEntryDao: HabitEntryDao
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        habitDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        habitEntryDao = habitDatabase.habitEntryDao()
        habitDao = habitDatabase.habitDao()

        runBlocking {
            val habit = HabitEntityFactory.createHabitEntity()
            habitDao.addHabit(habit)
        }

    }

    @After
    fun tearDown() {
        habitDatabase.close()
    }

    @Test
    fun add_entry_returns_entry_in_list() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        val result = habitEntryDao.getEntriesForHabit(habitEntryEntity.habitId).first()

        assertEquals(listOf(habitEntryEntity), result)
    }

    @Test
    fun get_entries_for_period_returns_entries_in_range() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        val entriesForPeriod = habitEntryDao.getEntriesForPeriod(
            habitEntryEntity.habitId,
            startDate = LocalDate.of(2026, 2, 1).toString(),
            endDate = LocalDate.of(2026, 2, 5).toString()
        ).first()

        assertEquals(listOf(habitEntryEntity), entriesForPeriod)
    }

    @Test
    fun get_entry_by_date_returns_correct_entry() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        val entriesForDate = habitEntryDao.getEntryByDate(
            habitEntryEntity.habitId,
            date = LocalDate.of(2026, 2, 2).toString()
        )

        assertEquals(habitEntryEntity, entriesForDate)
    }

    @Test
    fun update_entry_changes_isDone() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        habitEntryDao.updateEntry(
            habitId = habitEntryEntity.habitId,
            currentDate = LocalDate.of(2026, 2, 2).toString(),
            isDone = true,
            updatedAt = LocalDate.of(2026, 2, 2).toString()
        )
        val result = habitEntryDao.getEntryByDate(
            habitEntryEntity.habitId,
            date = LocalDate.of(2026, 2, 2).toString()
        )

        assertEquals(habitEntryEntity.copy(isDone = true, updatedAt = "2026-02-02"), result)
    }

    @Test
    fun mark_as_synced_sets_isSynced_true() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        habitEntryDao.markAsSynced(habitEntryEntity.id)

        val result = habitEntryDao.getEntryByDate(habitEntryEntity.habitId, habitEntryEntity.date)

        assertEquals(habitEntryEntity.copy(isSynced = true), result)
    }

    @Test
    fun get_unsynced_entries_returns_only_unsynced() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        val result = habitEntryDao.getUnsyncedEntries().first()

        assertEquals(listOf(habitEntryEntity), result)
    }

    @Test
    fun get_entries_for_date_returns_entries_on_date() = runTest {
        val habitEntryEntity = HabitEntryEntityFactory.createHabitEntryEntity()

        habitEntryDao.addEntry(habitEntryEntity)
        val result = habitEntryDao.getEntriesForDate(habitEntryEntity.date).first()

        assertEquals(listOf(habitEntryEntity), result)
    }


}
