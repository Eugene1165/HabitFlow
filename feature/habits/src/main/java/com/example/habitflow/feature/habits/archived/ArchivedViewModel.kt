package com.example.habitflow.feature.habits.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.DeleteHabitUseCase
import com.example.habitflow.domain.usecase.GetArchivedHabitsUseCase
import com.example.habitflow.domain.usecase.RestoreHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedViewModel @Inject constructor(
    private val restoreHabitUseCase: RestoreHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val getArchivedHabitsUseCase: GetArchivedHabitsUseCase
) : ViewModel() {

    private val _events = Channel<ArchivedEvent>()
    val events = _events.receiveAsFlow()


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
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = ArchivedUiState.Loading
        )

    fun onRestore(habitId: Int) {
        viewModelScope.launch {
            when (val result = restoreHabitUseCase.invoke(habitId)) {
                is HabitResult.Success -> result.data
                is HabitResult.Error -> _events.send(ArchivedEvent.ShowError(result.message))
            }
        }
    }

    fun onDelete(habitId: Int) {
        viewModelScope.launch {
            when (val result = deleteHabitUseCase.invoke(habitId)) {
                is HabitResult.Success -> result.data
                is HabitResult.Error -> _events.send(ArchivedEvent.ShowError(result.message))
            }
        }
    }
}
