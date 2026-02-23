package com.example.habitflow.di

import com.example.habitflow.data.local.preferences.UserPreferencesRepositoryImpl
import com.example.habitflow.data.repository.HabitEntryRepositoryImpl
import com.example.habitflow.data.repository.HabitRepositoryImpl
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

abstract class RepositoryModule {
    @Binds
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    abstract fun bindHabitEntryRepository(impl: HabitEntryRepositoryImpl): HabitEntryRepository

    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}