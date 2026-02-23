package com.example.habitflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.local.entity.HabitEntity
import com.example.habitflow.data.local.entity.HabitEntryEntity

@Database(entities = [HabitEntity::class, HabitEntryEntity::class], version = 1, exportSchema = false)
abstract class HabitDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getInstance(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context = context,
                    klass = HabitDatabase::class.java,
                    name = "habit_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
