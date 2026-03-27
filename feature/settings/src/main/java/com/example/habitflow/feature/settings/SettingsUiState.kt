package com.example.habitflow.feature.settings

import java.time.DayOfWeek

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Content(val isDarkTheme: Boolean, val firstDayOfWeek: DayOfWeek) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
