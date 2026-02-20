package com.example.habitflow.data.repository

import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitMapper: HabitMapper,
    private val dao: HabitDao,
) : HabitRepository {

    override fun getAllActiveHabits(): Flow<List<Habit>> {
        return dao.getAllActiveHabits()
            .map { list -> list.map { entity -> habitMapper.mapHabitEntityToHabit(entity) } }
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
        return dao.observeHabitById(habitId).map {
            entity -> entity?.let { habitMapper.mapHabitEntityToHabit(it) }
        }
    }

    override suspend fun addHabit(habit: Habit) {
        dao.addHabit(habitMapper.mapHabitToHabitEntity(habit))

    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habitMapper.mapHabitToHabitEntity(habit))
    }

    override suspend fun deleteHabit(habitId: Int) {
        dao.deleteHabit(habitId)
    }

    override suspend fun archiveHabit(habitId: Int) {
        dao.archiveHabit(habitId)
    }

    override suspend fun restoreHabit(habitId: Int) {
        dao.restoreHabit(habitId)
    }
}