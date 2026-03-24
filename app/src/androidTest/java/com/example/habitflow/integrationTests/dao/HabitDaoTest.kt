package com.example.habitflow.integrationTests.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.database.HabitDatabase
import com.example.habitflow.data.local.entity.HabitEntity
import com.example.habitflow.integrationTests.fixtures.HabitEntityFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var habitDatabase: HabitDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        habitDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        habitDao = habitDatabase.habitDao()
    }

    @After
    fun tearDown() {
        habitDatabase.close()
    }

    @Test
    fun insert_habit_and_get_all_active_habits() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 2)

        habitDao.addHabit(habitEntity)
        val result = habitDao.getAllActiveHabits().first()

        assertEquals(listOf(habitEntity), result)
    }

    @Test
    fun archive_habit_moves_habit_to_archived() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 2)

        habitDao.addHabit(habitEntity)
        habitDao.archiveHabit(habitEntity.id)

        val archivedResult = habitDao.getArchivedHabits().first()
        val activeResult = habitDao.getAllActiveHabits().first()

        assertEquals(listOf(habitEntity.copy(isArchived = true)), archivedResult)
        assertEquals(emptyList<HabitEntity>(), activeResult)
    }

    @Test
    fun restore_habit_moves_habit_to_active() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 2)

        habitDao.addHabit(habitEntity)
        habitDao.archiveHabit(habitEntity.id)
        habitDao.restoreHabit(habitEntity.id)

        val activeResult = habitDao.getAllActiveHabits().first()
        val archivedResult = habitDao.getArchivedHabits().first()

        assertEquals(emptyList<HabitEntity>(), archivedResult)
        assertEquals(listOf(habitEntity.copy(isArchived = false)), activeResult)
    }

    @Test
    fun delete_habit_removes_habit_from_db() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 3)

        habitDao.addHabit(habitEntity)
        habitDao.archiveHabit(habitEntity.id)
        habitDao.deleteHabit(habitEntity.id)

        val archivedResult = habitDao.getArchivedHabits().first()

        assertEquals(emptyList<HabitEntity>(), archivedResult)
    }

    @Test
    fun get_habit_by_id_returns_correct_habit() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 3)

        habitDao.addHabit(habitEntity)
        val habit = habitDao.getHabitById(habitEntity.id)

        assertEquals(habitEntity, habit)
    }

    @Test
    fun update_habit_changes_habit_title() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(title = "Test")

        habitDao.addHabit(habitEntity)
        habitDao.updateHabit(habitEntity.copy(title = "Test2"))

        val activeResult = habitDao.getAllActiveHabits().first()

        assertEquals(listOf(habitEntity.copy(title = "Test2")), activeResult)
    }

    @Test
    fun observe_habit_by_id_returns_correct_habit() = runTest {
        val habitEntity = HabitEntityFactory.createHabitEntity(id = 2)

        habitDao.addHabit(habitEntity)
        val observe = habitDao.observeHabitById(habitEntity.id).first()

        assertEquals(habitEntity, observe)
    }


}
