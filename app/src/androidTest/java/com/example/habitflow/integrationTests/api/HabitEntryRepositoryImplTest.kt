package com.example.habitflow.integrationTests.api

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.local.database.HabitDatabase
import com.example.habitflow.data.mapper.HabitEntryDtoMapper
import com.example.habitflow.data.mapper.HabitEntryMapper
import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.data.remote.api.HabitEntryApiService
import com.example.habitflow.data.repository.HabitEntryRepositoryImpl
import com.example.habitflow.integrationTests.fixtures.HabitEntryFactory
import com.example.habitflow.integrationTests.fixtures.HabitFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.first

class HabitEntryRepositoryImplTest {
    private lateinit var habitDatabase: HabitDatabase
    private lateinit var habitEntryRepositoryImpl: HabitEntryRepositoryImpl
    private lateinit var mockWebServer: MockWebServer
    private lateinit var habitEntryApiService: HabitEntryApiService
    private lateinit var habitEntryDao: HabitEntryDao
    private lateinit var habitDao: HabitDao

    private val habitEntryMapper = HabitEntryMapper()
    private val habitEntryDtoMapper = HabitEntryDtoMapper()
    private val habitMapper = HabitMapper()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        habitDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        habitEntryApiService = retrofit.create(HabitEntryApiService::class.java)
        habitEntryDao = habitDatabase.habitEntryDao()
        habitDao = habitDatabase.habitDao()
        habitEntryRepositoryImpl = HabitEntryRepositoryImpl(
            habitEntryDao,
            habitEntryMapper,
            habitEntryDtoMapper,
            habitEntryApiService
        )
    }

    @After
    fun teardown() {
        habitDatabase.close()
        mockWebServer.close()
    }

    @Test
    fun addEntry_success() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()
        val entryDomain = HabitEntryFactory.createHabitEntryDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitEntryRepositoryImpl.addEntry(entryDomain)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("POST", request.method)
        assertEquals("/rest/v1/habit_entries", request.path)
        assertTrue(body.contains("\"habit_id\":1"))
        assertTrue(body.contains("\"date\":\"2026-03-02\""))
        assertTrue(body.contains("\"is_done\":false"))

        val entries = habitEntryDao.getEntriesForHabit(entryDomain.habitId).first()
        assertEquals(1, entries.size)
        assertTrue(entries.first().isSynced)
    }

    @Test
    fun addEntry_serverError() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()
        val entryDomain = HabitEntryFactory.createHabitEntryDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitEntryRepositoryImpl.addEntry(entryDomain)
        val entries = habitEntryDao.getEntriesForHabit(entryDomain.habitId).first()

        assertEquals(1, entries.size)
        assertFalse(entries.first().isSynced)

    }

    @Test
    fun updateEntry_success() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()
        val entryDomain = HabitEntryFactory.createHabitEntryDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )
        habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitEntryDao.addEntry(habitEntryMapper.mapHabitEntryToHabitEntryEntity(entryDomain))
        habitEntryRepositoryImpl.updateEntry(
            habitId = entryDomain.habitId,
            currentDate = entryDomain.date,
            isDone = true,
            updatedAt = entryDomain.updatedAt
        )
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("PATCH", request.method)
        assertEquals("/rest/v1/habit_entries?id=eq.1", request.path)
        assertTrue(body.contains("\"is_done\":true"))

        val entries = habitEntryDao.getEntriesForHabit(entryDomain.habitId).first()

        assertTrue(entries.first().isSynced)
        assertEquals(true, entries.first().isDone)
    }

    @Test
    fun updateEntry_serverError() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()
        val entryDomain = HabitEntryFactory.createHabitEntryDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )
        habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitEntryDao.addEntry(habitEntryMapper.mapHabitEntryToHabitEntryEntity(entryDomain))
        habitEntryRepositoryImpl.updateEntry(
            habitId = entryDomain.habitId,
            currentDate = entryDomain.date,
            isDone = true,
            updatedAt = entryDomain.updatedAt
        )

        val entries = habitEntryDao.getEntriesForHabit(entryDomain.habitId).first()
        assertFalse(entries.first().isSynced)
    }
}
