package com.example.habitflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isOnBoardingCompleted(): Flow<Boolean>
    suspend fun setOnBoardingCompleted()
}