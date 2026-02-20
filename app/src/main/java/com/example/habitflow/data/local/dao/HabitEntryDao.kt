package com.example.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.habitflow.data.local.entity.HabitEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    @Insert
    suspend fun addEntry(entity: HabitEntryEntity): Unit

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate  ")
    fun getEntriesForPeriod(habitId:Int, startDate: String,endDate: String): Flow<List<HabitEntryEntity>>

    @Query("UPDATE habit_entries SET isDone = :isDone WHERE habitId = :habitId AND date = :currentDate")
    suspend fun updateEntry(habitId: Int,currentDate: String,isDone: Boolean): Unit

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun getEntryByDate(habitId: Int,date: String): HabitEntryEntity?

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId")
    fun getEntriesForHabit(habitId: Int): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<HabitEntryEntity>>
}