package com.example.habitflow.di

import com.example.habitflow.FakeHabitEntryRepository
import com.example.habitflow.FakeHabitRepository
import com.example.habitflow.FakeUserPreferencesRepository
import com.example.habitflow.data.repository.HabitEntryRepositoryImpl
import com.example.habitflow.data.repository.HabitRepositoryImpl
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {
    companion object {
        @Singleton
        @Provides
        fun provideFakeHabitEntryRepository(): HabitEntryRepository =
            FakeHabitEntryRepository()

        @Singleton
        @Provides
        fun provideFakeHabitRepository(): HabitRepository =
            FakeHabitRepository()

        @Singleton
        @Provides
        fun provideFakeUserPreferencesRepository(): UserPreferencesRepository =
            FakeUserPreferencesRepository()
    }
}