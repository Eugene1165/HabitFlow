package com.example.habitflow.presentation.statistics

import com.example.habitflow.domain.model.AllHabitsStatistics

sealed class StatisticsUiState {
    object Loading : StatisticsUiState()
    object Empty : StatisticsUiState()
    data class Error(val message: String) : StatisticsUiState()
    data class Content(val data: AllHabitsStatistics) : StatisticsUiState()
}