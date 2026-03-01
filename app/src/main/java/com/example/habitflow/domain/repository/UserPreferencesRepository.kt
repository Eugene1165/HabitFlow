package com.example.habitflow.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface UserPreferencesRepository {
    fun isOnBoardingCompleted(): Flow<Boolean>
    suspend fun setOnBoardingCompleted()

    fun isDarkThemeEnabled(): Flow<Boolean>

    suspend fun setDarkThemeEnabled(isEnabled: Boolean)

    fun getFirstDayOfWeek(): Flow<DayOfWeek>

    suspend fun setFirstDayOfWeek(day: DayOfWeek)
}