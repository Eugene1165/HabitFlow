package com.example.habitflow.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetDarkThemeUseCase
import com.example.habitflow.domain.usecase.GetFirstDayOfWeekUseCase
import com.example.habitflow.domain.usecase.SetDarkThemeUseCase
import com.example.habitflow.domain.usecase.SetFirstDayOfWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    val state: StateFlow<SettingsUiState> = combine(
        getDarkThemeUseCase(),
        getFirstDayOfWeekUseCase()
    ) { isDark, firstDay ->
        SettingsUiState.Content(
            isDarkTheme = isDark,
            firstDayOfWeek = firstDay
        ) as SettingsUiState
    }
        .catch { e -> emit(SettingsUiState.Error(e.message ?: "Ошибка загрузки")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = SUBSCRIBE_TIMEOUT_MS),
            initialValue = SettingsUiState.Loading
        )

    fun onDarkThemeToggled(isEnabled: Boolean) {
        viewModelScope.launch { setDarkThemeUseCase(isEnabled) }
    }

    fun onFirstDayOfWeekChanged(day: DayOfWeek) {
        viewModelScope.launch { setFirstDayOfWeekUseCase(day) }
    }

    companion object{
        private const val SUBSCRIBE_TIMEOUT_MS = 5000L
    }


}
