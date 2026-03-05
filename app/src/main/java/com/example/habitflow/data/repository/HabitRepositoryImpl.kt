package com.example.habitflow.data.repository

import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.mapper.HabitDtoMapper
import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.data.remote.api.HabitApiService
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.lang.Exception
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitMapper: HabitMapper,
    private val dao: HabitDao,
    private val habitApiService: HabitApiService,
    private val habitDtoMapper: HabitDtoMapper,
) : HabitRepository {

    override fun getAllActiveHabits(): Flow<List<Habit>> {
        return dao.getAllActiveHabits()
            .map { list -> list.map { entity -> habitMapper.mapHabitEntityToHabit(entity) } }
            .onStart {
                try {
                    val dtos = habitApiService.getAllEntries()
                    dtos.forEach { dto ->
                        habitDtoMapper.mapDtoToHabit(dto)
                            .let { habitMapper.mapHabitToHabitEntity(it) }
                            .let { dao.addHabit(it) }
                    }
                }catch (_: kotlin.Exception){}
            }
    }

    override fun getArchivedHabits(): Flow<List<Habit>> {
        return dao.getArchivedHabits()
            .map { list -> list.map { entity -> habitMapper.mapHabitEntityToHabit(entity) } }
    }

    override suspend fun getHabitById(habitId: Int): Habit? {
        return dao.getHabitById(habitId)?.let {
            habitMapper.mapHabitEntityToHabit(it)
        }

    }

    override fun observeHabitById(habitId: Int): Flow<Habit?> {
        return dao.observeHabitById(habitId).map { entity ->
            entity?.let { habitMapper.mapHabitEntityToHabit(it) }
        }
    }

    override suspend fun addHabit(habit: Habit) {
        val generatedId = dao.addHabit(habitMapper.mapHabitToHabitEntity(habit))
        val habitWithId = habit.copy(id = generatedId.toInt())
        try {
            habitApiService.createEntries(habitDtoMapper.mapHabitToDto(habitWithId))
        } catch (e: Exception) { }

    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habitMapper.mapHabitToHabitEntity(habit))
        try {
            habitApiService.updateEntriesById(
                habit.id.toString(),
                habitDtoMapper.mapHabitToDto(habit)
            )
        } catch (_: Exception){}
    }

    override suspend fun deleteHabit(habitId: Int) {
        dao.deleteHabit(habitId)
        try {
            habitApiService.removeEntriesById(habitId.toString())
        } catch (_: Exception) {
        }
    }

    override suspend fun archiveHabit(habitId: Int) {
        dao.archiveHabit(habitId)
    }

    override suspend fun restoreHabit(habitId: Int) {
        dao.restoreHabit(habitId)
    }
}