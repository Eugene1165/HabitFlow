package com.example.habitflow.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetAllHabitsStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllHabitsStatisticsUseCase: GetAllHabitsStatisticsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            getAllHabitsStatisticsUseCase.invoke()
                .catch { e ->
                    _state.value = StatisticsUiState.Error(e.message ?: "Ошибка загрузки")
                }
                .collect { stats ->
                    _state.value = when {
                        stats == null -> StatisticsUiState.Empty
                        else -> StatisticsUiState.Content(stats)
                    }
                }
        }
    }
}