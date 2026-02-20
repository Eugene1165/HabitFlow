package com.example.habitflow.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsListViewModel @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<HabitsListUiState>(HabitsListUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                getAllActiveHabitsUseCase().collect { habits ->
                    _state.value = if (habits.isEmpty()){
                        HabitsListUiState.Empty
                    } else {
                        HabitsListUiState.Content(habits)
                    }
                }
            } catch (e: Exception) {
                _state.value = HabitsListUiState.Error(e.message ?: "Ошибка")
            }
        }
    }




}