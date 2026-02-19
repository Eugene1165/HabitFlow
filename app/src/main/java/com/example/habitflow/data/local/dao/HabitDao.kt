package com.example.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habitflow.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE isArchived = 0")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits  WHERE isArchived = 1 ")
    fun getArchivedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): HabitEntity?

    @Query("SELECT * FROM habits WHERE id=:habitId ")
    fun observeHabitById(habitId: Int): Flow<HabitEntity?>

    @Insert
    suspend fun addHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id=:habitId")
    suspend fun deleteHabit(habitId: Int)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :habitId ")
    suspend fun archiveHabit(habitId: Int)

    @Query("UPDATE habits SET isArchived = 0 WHERE id = :habitId")
    suspend fun restoreHabit(habitId: Int)
}