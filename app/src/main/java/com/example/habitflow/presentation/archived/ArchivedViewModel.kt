package com.example.habitflow.presentation.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.DeleteHabitUseCase
import com.example.habitflow.domain.usecase.GetArchivedHabitsUseCase
import com.example.habitflow.domain.usecase.RestoreHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedViewModel @Inject constructor(
    private val restoreHabitUseCase: RestoreHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val getArchivedHabitsUseCase: GetArchivedHabitsUseCase
) : ViewModel() {
    val state: StateFlow<ArchivedUiState> = getArchivedHabitsUseCase()
        .map { habits ->
            when {
                habits.isEmpty() -> ArchivedUiState.Empty
                else -> ArchivedUiState.Content(habits)
            }
        }
        .catch { e -> emit(ArchivedUiState.Error(e.message ?: "привычек не найдено")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ArchivedUiState.Loading
        )

    fun onRestore(habitId: Int) {
        viewModelScope.launch { restoreHabitUseCase.invoke(habitId) }
    }

    fun onDelete(habitId: Int) {
        viewModelScope.launch { deleteHabitUseCase.invoke(habitId) }
    }

}