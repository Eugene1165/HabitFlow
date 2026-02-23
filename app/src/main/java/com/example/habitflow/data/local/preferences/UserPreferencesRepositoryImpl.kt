package com.example.habitflow.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.habitflow.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")

    override fun isOnBoardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[ONBOARDING_KEY] ?: false }

    }

    override suspend fun setOnBoardingCompleted() {
        dataStore.edit { preferences -> preferences[ONBOARDING_KEY] = true}
    }
}