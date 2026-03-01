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
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    private val FIRST_DAY_OF_WEEK_KEY = stringPreferencesKey("first_day_of_week")

    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[ONBOARDING_KEY] ?: false }

    }

    override suspend fun setOnBoardingCompleted() {
        dataStore.edit { preferences -> preferences[ONBOARDING_KEY] = true }
    }

    override fun isDarkThemeEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[DARK_THEME_KEY] ?: false }
    }

    override suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences -> preferences[DARK_THEME_KEY] = isEnabled }
    }

    override fun getFirstDayOfWeek(): Flow<DayOfWeek> {
        return dataStore.data.map { preferences ->
            val str = preferences[FIRST_DAY_OF_WEEK_KEY] ?: "MONDAY"
            DayOfWeek.valueOf(str) }
    }

    override suspend fun setFirstDayOfWeek(day: DayOfWeek) {
        dataStore.edit { preferences -> preferences[FIRST_DAY_OF_WEEK_KEY] = day.name }
    }
}