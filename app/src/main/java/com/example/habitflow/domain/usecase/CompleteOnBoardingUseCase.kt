package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class CompleteOnBoardingUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(){
        return repository.setOnBoardingCompleted()
    }
}