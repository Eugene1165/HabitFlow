package com.example.habitflow.di

import com.example.habitflow.data.di.RepositoryModule
import com.example.habitflow.data.di.WorkManagerModule
import com.example.habitflow.fakeRepository.FakeHabitEntryRepository
import com.example.habitflow.fakeRepository.FakeHabitRepository
import com.example.habitflow.fakeRepository.FakeReminderScheduler
import com.example.habitflow.fakeRepository.FakeUserPreferencesRepository
import com.example.habitflow.domain.repository.HabitEntryRepository
import com.example.habitflow.domain.repository.HabitRepository
import com.example.habitflow.domain.repository.UserPreferencesRepository
import com.example.habitflow.domain.scheduler.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class, WorkManagerModule::class]
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

        @Singleton
        @Provides
        fun provideFakeReminderScheduler(): ReminderScheduler =
            FakeReminderScheduler()
    }
}
