package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean>{
        return repository.isDarkThemeEnabled()
    }
}