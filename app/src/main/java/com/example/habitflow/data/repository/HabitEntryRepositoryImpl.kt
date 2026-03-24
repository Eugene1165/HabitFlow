package com.example.habitflow.data.repository

import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.mapper.HabitEntryDtoMapper
import com.example.habitflow.data.mapper.HabitEntryMapper
import com.example.habitflow.data.remote.api.HabitEntryApiService
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import retrofit2.HttpException
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class HabitEntryRepositoryImpl @Inject constructor(
    private val dao: HabitEntryDao,
    private val habitEntryMapper: HabitEntryMapper,
    private val habitEntryDtoMapper: HabitEntryDtoMapper,
    private val habitEntryApiService: HabitEntryApiService
) : HabitEntryRepository {

    override suspend fun addEntry(entry: HabitEntry) {
        val generatedId = dao.addEntry(habitEntryMapper.mapHabitEntryToHabitEntryEntity(entry))
        val entryWithId = entry.copy(id = generatedId.toInt())
        try {
            habitEntryApiService.createEntry(habitEntryDtoMapper.mapHabitEntryToDto(entryWithId))
            dao.markAsSynced(entryWithId.id)
        } catch (e: IOException) {
            Timber.e(e, "нет сети")
        } catch (e: HttpException){
            Timber.e(e,"Не удалось добавить записи для привычки")
        }
    }

    override fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntry>> {
        return dao.getEntriesForHabit(habitId)
            .map { list -> list.map { habitEntryMapper.mapHabitEntryEntityToHabitEntry(it) } }
            .onStart {
                try {
                    val dtos = habitEntryApiService.getEntriesByHabitId("eq.${habitId}")
                    val entities = dtos.map { dto ->
                        habitEntryDtoMapper.mapDtoToHabitEntry(dto)
                            .let {
                                habitEntryMapper.mapHabitEntryToHabitEntryEntity(
                                    it,
                                    isSynced = true
                                )
                            }
                    }
                    dao.insertAll(entities)
                } catch (e: IOException) {
                    Timber.e(e, "Не удалось получить  записи для привычки")
                } catch (e: okio.IOException){
                    Timber.e(e,"Нет сети")
                }

                val unsynced = dao.getUnsyncedEntries().first()
                unsynced.forEach { entity ->
                    try {
                        val entry = habitEntryMapper.mapHabitEntryEntityToHabitEntry(entity)
                        val dto = habitEntryDtoMapper.mapHabitEntryToDto(entry)
                        habitEntryApiService.createEntry(dto)
                        dao.markAsSynced(entity.id)
                    } catch (e: IOException) {
                        Timber.e(e, "нет сети")
                    } catch (e: HttpException){
                        Timber.e(e,"Ошибка retry синхронизации записи \${entity.id}")
                    }
                }
            }
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
        isDone: Boolean,
        updatedAt: LocalDateTime
    ) {
        dao.updateEntry(habitId, currentDate.toString(), isDone,updatedAt.toString())
        try {
            val entry = dao.getEntryByDate(habitId, currentDate.toString()) ?: return
            val domainEntity = habitEntryMapper.mapHabitEntryEntityToHabitEntry(entry)
            val dto = habitEntryDtoMapper.mapHabitEntryToDto(domainEntity)
            habitEntryApiService.updateEntryById("eq.${entry.id}", dto)
            dao.markAsSynced(entry.id)
        } catch (e: IOException) {
            Timber.e(e, "нет сети")
        } catch (e: HttpException){
            Timber.e(e,"Не удалось обновить записи для привычки")
        }
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
