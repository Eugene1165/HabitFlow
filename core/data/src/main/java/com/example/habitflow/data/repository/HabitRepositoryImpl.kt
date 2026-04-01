package com.example.habitflow.data.repository

import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.mapper.HabitDtoMapper
import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.data.remote.api.HabitApiService
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitMapper: HabitMapper,
    private val dao: HabitDao,
    private val habitApiService: HabitApiService,
    private val habitDtoMapper: HabitDtoMapper,
) : HabitRepository {

    override fun getAllActiveHabits(): Flow<HabitResult<List<Habit>>> =
        //запускаем рум поток сначала поскольку стратегия offline-firsst
        dao.getAllActiveHabits()
            .map { list ->
                HabitResult.Success(list.map { entity -> habitMapper.mapHabitEntityToHabit(entity) })
            }
            .onStart {
                try {
                    val dtos = habitApiService.getAllEntries()
                    dtos.forEach { dto ->
                        habitDtoMapper.mapDtoToHabit(dto)
                            .let { habitMapper.mapHabitToHabitEntity(it) }
                            .let { dao.addHabit(it) }
                    }

                } catch (e: IOException) {
                    Timber.e(e, "Не удалось получить список активных привычек")

                } catch (e: HttpException) {
                    Timber.e(e, "Нет сети")
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

    override suspend fun addHabit(habit: Habit): HabitResult<Habit> {
        val generatedId = dao.addHabit(habitMapper.mapHabitToHabitEntity(habit))
        val habitWithId = habit.copy(id = generatedId.toInt())
        return try {
            val response = habitApiService.createEntries(habitDtoMapper.mapHabitToDto(habitWithId))
            if (response.isSuccessful) HabitResult.Success(habitWithId)
            else HabitResult.Error(Exception(), "Не удалось добавить привычку")
        } catch (e: IOException) {
            Timber.e(e, "Не удалось добавить привычку")
            HabitResult.Error(e, "Не удалось добавить привычку")
        } catch (e: HttpException) {
            Timber.e(e, "Нет сети")
            HabitResult.Error(e, "Нет сети")
        }
    }

    override suspend fun updateHabit(habit: Habit): HabitResult<Unit> {
        dao.updateHabit(habitMapper.mapHabitToHabitEntity(habit))
        return try {
            val response = habitApiService.updateEntriesById(
                habit.id.toString(),
                habitDtoMapper.mapHabitToDto(habit)
            )
            if (response.isSuccessful) HabitResult.Success(Unit)
            else HabitResult.Error(Exception(), "Не удалось обновить привычку")
        } catch (e: IOException) {
            Timber.e(e, "Не удалось обновить привычку")
            HabitResult.Error(e, "Не удалось обновить привычку")
        } catch (e: HttpException) {
            Timber.e(e, "Нет сети")
            HabitResult.Error(e, "Нет сети")
        }
    }

    override suspend fun deleteHabit(habitId: Int): HabitResult<Unit> {
        dao.deleteHabit(habitId)
        return try {
            val response = habitApiService.removeEntriesById(habitId.toString())
            if (response.isSuccessful) HabitResult.Success(Unit)
            else HabitResult.Error(Exception(), "Не удалось удалить привычку")
        } catch (e: IOException) {
            Timber.e(e, "Не удалось удалить привычку")
            HabitResult.Error(e, "Не удалось удалить привычку")
        } catch (e: HttpException) {
            Timber.e(e, "Нет сети")
            HabitResult.Error(e, "Нет сети")
        }
    }

    override suspend fun archiveHabit(habitId: Int): HabitResult<Unit> {
        dao.archiveHabit(habitId)
        return try {
            val entity = dao.getHabitById(habitId) ?: return HabitResult.Error(
                IllegalStateException("Привычка не найдена"),
                "Привычка не найдена"
            )
            val habit = habitMapper.mapHabitEntityToHabit(entity).copy(isArchived = true)
            val habitDto = habitDtoMapper.mapHabitToDto(habit)
            val response = habitApiService.updateEntriesById(habit.id.toString(), habitDto)
            if (response.isSuccessful) HabitResult.Success(Unit)
            else HabitResult.Error(Exception(), "Не удалось заархивировать привычку")
        } catch (e: IOException) {
            Timber.e(e, "Не удалось заархивировать привычку")
            HabitResult.Error(e, "Не удалось заархивировать привычку")
        } catch (e: HttpException) {
            Timber.e(e, "Нет сети")
            HabitResult.Error(e, "Нет сети")
        }
    }

    override suspend fun restoreHabit(habitId: Int): HabitResult<Unit> {
        dao.restoreHabit(habitId)
        return try {
            val entity = dao.getHabitById(habitId) ?: return HabitResult.Error(
                IllegalStateException("Привычка не найдена"),
                "Привычка не найдена"
            )
            val habit = habitMapper.mapHabitEntityToHabit(entity).copy(isArchived = false)
            val habitDto = habitDtoMapper.mapHabitToDto(habit)
            val response = habitApiService.updateEntriesById(habit.id.toString(), habitDto)
            if(response.isSuccessful) HabitResult.Success(Unit)
            else HabitResult.Error(Exception(), "Не удалось восстановить из архива привычку")
        } catch (e: IOException) {
            Timber.e(e, "Не удалось восстановить из архива привычку")
            HabitResult.Error(e, "Не удалось восстановить из архива привычку")
        } catch (e: HttpException) {
            Timber.e(e, "Нет сети")
            HabitResult.Error(e, "Не удалось восстановить из архива привычку")
        }
    }
}
