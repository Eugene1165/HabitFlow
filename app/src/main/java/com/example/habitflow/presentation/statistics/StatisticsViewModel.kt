package com.example.habitflow.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetAllHabitsStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllHabitsStatisticsUseCase: GetAllHabitsStatisticsUseCase
) : ViewModel() {
    val state: StateFlow<StatisticsUiState> = getAllHabitsStatisticsUseCase()
        .map { stats ->
            when {
                stats == null -> StatisticsUiState.Empty
                else -> StatisticsUiState.Content(stats)
            }
        }
        .catch { e -> emit(StatisticsUiState.Error(e.message ?: "Ошибка загрузки")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = StatisticsUiState.Loading
        )
}
