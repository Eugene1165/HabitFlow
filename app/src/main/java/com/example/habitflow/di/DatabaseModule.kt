package com.example.habitflow.di

import android.content.Context
import androidx.room.Room
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.local.database.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habit_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Singleton
    @Provides
    fun provideHabitEntryDao(database: HabitDatabase): HabitEntryDao {
        return database.habitEntryDao()
    }


}