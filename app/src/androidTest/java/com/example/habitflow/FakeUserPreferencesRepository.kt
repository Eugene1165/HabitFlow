package com.example.habitflow

import com.example.habitflow.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek

// FakeUserPreferencesRepository — это коробка с переменными.
// Методы просто читают и записывают эти переменные.
class FakeUserPreferencesRepository : UserPreferencesRepository {

    private val _onBoardingCompleted = MutableStateFlow(true)

    override fun isOnBoardingCompleted(): MutableStateFlow<Boolean> = _onBoardingCompleted

    override suspend fun setOnBoardingCompleted() {
        _onBoardingCompleted.value = true
    }

    private val _isDarkThemeEnabled = MutableStateFlow(false)

    override fun isDarkThemeEnabled(): MutableStateFlow<Boolean> = _isDarkThemeEnabled

    override suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        _isDarkThemeEnabled.value = isEnabled
    }

    private val _firstDayOfWeek = MutableStateFlow(DayOfWeek.MONDAY)
    override fun getFirstDayOfWeek(): MutableStateFlow<DayOfWeek> =_firstDayOfWeek

    override suspend fun setFirstDayOfWeek(day: DayOfWeek) {
       _firstDayOfWeek.value = day
    }
}