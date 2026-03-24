package com.example.habitflow.data.local.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.mapper.HabitEntryDtoMapper
import com.example.habitflow.data.mapper.HabitEntryMapper
import com.example.habitflow.data.remote.api.HabitEntryApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitEntryDao: HabitEntryDao,
    private val habitEntryApiService: HabitEntryApiService,
    private val habitEntryMapper: HabitEntryMapper,
    private val habitEntryDtoMapper: HabitEntryDtoMapper,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val entity = habitEntryDao.getUnsyncedEntries().first()
        entity.forEach {
            try {
                val habitEntry = habitEntryMapper.mapHabitEntryEntityToHabitEntry(it)
                val habitDto = habitEntryDtoMapper.mapHabitEntryToDto(habitEntry)
                habitEntryApiService.updateEntryById(it.id.toString(),habitDto)
                habitEntryDao.markAsSynced(it.id)
            }catch (e: Exception){
                Timber.e(e, "Ошибка синхронизации записи ${it.id}")
            }
        }
        return Result.success()
    }
}



