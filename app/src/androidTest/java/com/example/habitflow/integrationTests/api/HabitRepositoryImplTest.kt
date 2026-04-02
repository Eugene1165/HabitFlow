package com.example.habitflow.integrationTests.api

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.database.HabitDatabase
import com.example.habitflow.data.mapper.HabitDtoMapper
import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.data.remote.api.HabitApiService
import com.example.habitflow.data.repository.HabitRepositoryImpl
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.integrationTests.fixtures.HabitFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.collections.first

class HabitRepositoryImplTest {
    private lateinit var habitDatabase: HabitDatabase
    private lateinit var habitRepositoryImpl: HabitRepositoryImpl
    private lateinit var mockWebServer: MockWebServer
    private lateinit var habitApiService: HabitApiService
    private lateinit var habitDao: HabitDao
    private val habitMapper = HabitMapper()
    private val habitDtoMapper = HabitDtoMapper()

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


        habitApiService = retrofit.create(HabitApiService::class.java)
        habitDao = habitDatabase.habitDao()
        habitRepositoryImpl =
            HabitRepositoryImpl(habitMapper, habitDao, habitApiService, habitDtoMapper)


    }

    @After
    fun tearDown() {
        habitDatabase.close()
        mockWebServer.close()
    }


    @Test
    fun addHabit_success() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val result = habitRepositoryImpl.addHabit(habit)
        val habits = habitDao.getAllActiveHabits().first()
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("POST",request.method)
        assertEquals("/rest/v1/Habit",request.path)
        assertTrue(body.contains("test"))
        assertTrue(body.contains("#F44336"))
        assertTrue(body.contains(LocalDate.of(2026, 3, 2).toString()))

        assertTrue("Expected Success but got: $result", result is HabitResult.Success)
        assertEquals(1, habits.size)
        assertEquals("test", habits.first().title)
    }

    @Test
    fun addHabit_serverError() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val result = habitRepositoryImpl.addHabit(habit)
        val habits = habitDao.getAllActiveHabits().first()
        val error = result as HabitResult.Error
        assertEquals(1, habits.size)
        assertEquals("Не удалось добавить привычку", error.message)
    }

    @Test
    fun addHabit_timeout() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        val result = habitRepositoryImpl.addHabit(habit)
        val habits = habitDao.getAllActiveHabits().first()
        val error = result as HabitResult.Error
        assertEquals(1, habits.size)
        assertEquals("Не удалось добавить привычку", error.message)
    }

    @Test
    fun addHabit_slowResponse() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setBodyDelay(5000, TimeUnit.MILLISECONDS)
                .setResponseCode(200)
                .setBody("")
        )

        val result = habitRepositoryImpl.addHabit(habit)
        val habits = habitDao.getAllActiveHabits().first()

        assertTrue("Expected Success but got: $result", result is HabitResult.Success)
        assertEquals(1, habits.size)
        assertEquals("test", habits.first().title)
    }

    @Test
    fun updateHabit_success() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val id = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habit))
        val updatedHabit = habit.copy(id = id.toInt(), title = "new title")
        val result = habitRepositoryImpl.updateHabit(updatedHabit)
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("PATCH",request.method)
        assertEquals("/rest/v1/Habit?id=1",request.path)
        assertTrue(body.contains("new title"))
        assertTrue(body.contains("#F44336"))
        assertTrue(body.contains(LocalDate.of(2026, 3, 2).toString()))

        val success = result as HabitResult.Success
        val fromDao = habitDao.getHabitById(id.toInt())
        assertEquals(HabitResult.Success(Unit), success)
        assertEquals("new title", fromDao?.title)
    }

    @Test
    fun updateHabit_serverError() = runBlocking {
        val habit = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val id = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habit))
        val updatedHabit = habit.copy(id = id.toInt(), title = "new title")
        val result = habitRepositoryImpl.updateHabit(updatedHabit)
        val error = result as HabitResult.Error

        assertEquals("Не удалось обновить привычку", error.message)
    }

    @Test
    fun deleteHabit_success() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        val result = habitRepositoryImpl.deleteHabit(habit.toInt())
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("DELETE",request.method)
        assertEquals("/rest/v1/Habit?id=1",request.path)
        val fromDao = habitDao.getHabitById(habit.toInt())
        val success = result as HabitResult.Success

        assertEquals(HabitResult.Success(Unit), success)
        assertNull(fromDao)
    }

    @Test
    fun deleteHabit_serverError() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        val result = habitRepositoryImpl.deleteHabit(habit.toInt())
        val fromDao = habitDao.getHabitById(habit.toInt())
        val error = result as HabitResult.Error

        assertEquals("Не удалось удалить привычку", error.message)
        assertNull(fromDao)
    }

    @Test
    fun archiveHabit_success() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        val result = habitRepositoryImpl.archiveHabit(habit.toInt())
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("PATCH",request.method)
        assertEquals("/rest/v1/Habit?id=1",request.path)
        assertTrue(body.contains("\"is_archived\":true"))

        val fromDao = habitDao.getHabitById(habit.toInt())
        val success = result as HabitResult.Success
        assertTrue(fromDao?.isArchived == true)
        assertEquals(HabitResult.Success(Unit), success)
    }

    @Test
    fun archiveHabit_serverError() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        val result = habitRepositoryImpl.archiveHabit(habit.toInt())
        val fromDao = habitDao.getHabitById(habit.toInt())
        val error = result as HabitResult.Error

        assertTrue(fromDao?.isArchived == true)
        assertEquals("Не удалось заархивировать привычку", error.message)
    }

    @Test
    fun restoreHabit_success() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitDao.archiveHabit(habit.toInt())
        val result = habitRepositoryImpl.restoreHabit(habit.toInt())
        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("PATCH",request.method)
        assertEquals("/rest/v1/Habit?id=1",request.path)
        assertTrue(body.contains("\"is_archived\":false"))

        val fromDao = habitDao.getHabitById(habit.toInt())
        val success = result as HabitResult.Success

        assertTrue(fromDao?.isArchived == false)
        assertEquals(HabitResult.Success(Unit), success)

    }

    @Test
    fun restoreHabit_error() = runBlocking {
        val habitDomain = HabitFactory.createHabitDomain()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val habit = habitDao.addHabit(habitMapper.mapHabitToHabitEntity(habitDomain))
        habitDao.archiveHabit(habit.toInt())
        val result = habitRepositoryImpl.restoreHabit(habit.toInt())
        val fromDao = habitDao.getHabitById(habit.toInt())
        val error = result as HabitResult.Error

        assertTrue(fromDao?.isArchived == false)
        assertEquals("Не удалось восстановить из архива привычку", error.message)

    }
}
