package com.example.habitflow.presentation.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.DeleteHabitUseCase
import com.example.habitflow.domain.usecase.GetArchivedHabitsUseCase
import com.example.habitflow.domain.usecase.RestoreHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedViewModel @Inject constructor(
    private val restoreHabitUseCase: RestoreHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val getArchivedHabitsUseCase: GetArchivedHabitsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ArchivedUiState>(ArchivedUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getArchivedHabitsUseCase()
                .catch { e ->
                    _state.value = ArchivedUiState.Error(e.message ?: "ошибка загрузки привычек")
                }
                .collect { habits ->
                    _state.value = if (habits.isEmpty()) ArchivedUiState.Empty
                    else ArchivedUiState.Content(habits)
                }
        }
    }

    fun onRestore(habitId: Int) {
        viewModelScope.launch { restoreHabitUseCase.invoke(habitId) }
    }

    fun onDelete(habitId: Int) {
        viewModelScope.launch { deleteHabitUseCase.invoke(habitId) }
    }

}