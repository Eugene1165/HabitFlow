package com.example.habitflow.presentation.habits.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import com.example.habitflow.domain.usecase.GetEntriesForDateUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitsListViewModel @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getEntriesForDateUseCase: GetEntriesForDateUseCase,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase,
) : ViewModel() {

    val state: StateFlow<HabitsListUiState> = combine(
        getAllActiveHabitsUseCase(),
        getEntriesForDateUseCase(LocalDate.now())
    ) { habits, entries ->
        //сюда приходят ПОСЛЕДНИЕ значения из обоих flow
        // здесь ты просто ВОЗВРАЩАЕШЬ новое состояние
        if (habits.isEmpty()) HabitsListUiState.Empty
        else {
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
        .catch { e -> emit(HabitsListUiState.Error(e.message ?: "Ошибка")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = HabitsListUiState.Loading
        )
    private val togglingHabits = mutableSetOf<Int>()
    fun onToggle(habitId: Int) {
        if (habitId in togglingHabits) return
        togglingHabits.add(habitId)
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                toggleHabitEntryUseCase.invoke(habitId, today)
            } finally {
                togglingHabits.remove(habitId)
            }
        }
    }
}
