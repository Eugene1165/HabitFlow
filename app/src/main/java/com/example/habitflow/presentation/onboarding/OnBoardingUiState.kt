package com.example.habitflow.presentation.onboarding

sealed class OnBoardingUiState {
    object Loading: OnBoardingUiState()
    object Content: OnBoardingUiState()
    data class Error(val message: String): OnBoardingUiState()
}