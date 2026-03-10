package com.example.habitflow.di

import com.example.habitflow.data.local.workmanager.WorkManagerReminderScheduler
import com.example.habitflow.domain.scheduler.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkManagerModule {
    @Binds
    abstract fun bindWorkManagerReminderScheduler(impl: WorkManagerReminderScheduler): ReminderScheduler
}