package com.example.habitflow.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.habitflow.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private val onBoardingKey = booleanPreferencesKey("onboarding_completed")
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val firstDayOfWeekDay = stringPreferencesKey("first_day_of_week")

    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[onBoardingKey] ?: false }

    }

    override suspend fun setOnBoardingCompleted() {
        dataStore.edit { preferences -> preferences[onBoardingKey] = true }
    }

    override fun isDarkThemeEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[darkThemeKey] ?: false }
    }

    override suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences -> preferences[darkThemeKey] = isEnabled }
    }

    override fun getFirstDayOfWeek(): Flow<DayOfWeek> {
        return dataStore.data.map { preferences ->
            val str = preferences[firstDayOfWeekDay] ?: "MONDAY"
            DayOfWeek.valueOf(str) }
    }

    override suspend fun setFirstDayOfWeek(day: DayOfWeek) {
        dataStore.edit { preferences -> preferences[firstDayOfWeekDay] = day.name }
    }
}
