package com.example.habitflow.domain.usecase


import com.example.habitflow.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetDarkThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(isEnabled: Boolean){
        return repository.setDarkThemeEnabled(isEnabled)
    }
}
