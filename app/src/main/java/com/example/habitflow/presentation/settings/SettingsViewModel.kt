package com.example.habitflow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetDarkThemeUseCase
import com.example.habitflow.domain.usecase.GetFirstDayOfWeekUseCase
import com.example.habitflow.domain.usecase.SetDarkThemeUseCase
import com.example.habitflow.domain.usecase.SetFirstDayOfWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getDarkThemeUseCase: GetDarkThemeUseCase,
    private val getFirstDayOfWeekUseCase: GetFirstDayOfWeekUseCase,
    private val setDarkThemeUseCase: SetDarkThemeUseCase,
    private val setFirstDayOfWeekUseCase: SetFirstDayOfWeekUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getDarkThemeUseCase(),
                getFirstDayOfWeekUseCase()
            ) { isDark, firstDay ->
                SettingsUiState.Content(
                    isDarkTheme = isDark,
                    firstDayOfWeek = firstDay
                )
            }
                .catch { e -> _state.value = SettingsUiState.Error(e.message ?: "Ошибка загрузки") }
                .collect { newContent ->
                    _state.value = newContent
                }
        }
    }


    fun onDarkThemeToggled(isEnabled: Boolean) {
        viewModelScope.launch { setDarkThemeUseCase(isEnabled) }
    }

    fun onFirstDayOfWeekChanged(day: DayOfWeek) {
        viewModelScope.launch { setFirstDayOfWeekUseCase(day) }
    }


}