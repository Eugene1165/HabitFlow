package com.example.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitflow.data.local.entity.HabitEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEntry(entity: HabitEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<HabitEntryEntity>)

    @Query("SELECT * FROM habit_entries " +
            "WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate  ")
    fun getEntriesForPeriod(
        habitId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<HabitEntryEntity>>

    @Query("UPDATE habit_entries SET isDone = :isDone, updatedAt = :updatedAt  " +
            "WHERE habitId = :habitId AND date = :currentDate")
    suspend fun updateEntry(
        habitId: Int,
        currentDate: String,
        isDone: Boolean,
        updatedAt: String
    ): Unit

    @Query("UPDATE habit_entries SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("SELECT * FROM habit_entries WHERE isSynced = 0 ")
    fun getUnsyncedEntries(): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun getEntryByDate(habitId: Int, date: String): HabitEntryEntity?

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId")
    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<HabitEntryEntity>>
}
