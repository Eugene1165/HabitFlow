package com.example.habitflow.di

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
    @Binds
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    abstract fun bindHabitEntryRepository(impl: HabitEntryRepositoryImpl): HabitEntryRepository

    companion object {
        @Singleton
        @Provides
        fun provideFakeUserPreferencesRepository(): UserPreferencesRepository =
            FakeUserPreferencesRepository()
    }
}