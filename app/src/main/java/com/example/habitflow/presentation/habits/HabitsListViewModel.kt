package com.example.habitflow.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import com.example.habitflow.domain.usecase.GetEntriesForDateUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitsListViewModel @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getEntriesForDateUseCase: GetEntriesForDateUseCase,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<HabitsListUiState>(HabitsListUiState.Loading)
    val state = _state.asStateFlow()

    init {
        val today = LocalDate.now()
        combine(
            getAllActiveHabitsUseCase(),
            getEntriesForDateUseCase(today)
        ) { habits, entries ->
            //сюда приходят ПОСЛЕДНИЕ значения из обоих flow
            // здесь ты просто ВОЗВРАЩАЕШЬ новое состояние
            if (habits.isEmpty()) {
                HabitsListUiState.Empty
            } else {
                // для каждой привычки проверяем — есть ли она в entries
                val habitsWithStatus = habits.map { habit ->
                    HabitWithStatus(
                        habit = habit,
                        isCompletedToday = entries.any { it.habitId == habit.id && it.isDone }
                    )
                }
                HabitsListUiState.Content(habitsWithStatus)
            }
        }
            .onEach { newState -> _state.value = newState }
            .catch { e -> _state.value = HabitsListUiState.Error(e.message ?: "Ошибка") }
            .launchIn(viewModelScope)
    }

    fun onToggle(habitId: Int){
        viewModelScope.launch {
            val today = LocalDate.now()
            toggleHabitEntryUseCase.invoke(habitId,today)
        }
    }


}