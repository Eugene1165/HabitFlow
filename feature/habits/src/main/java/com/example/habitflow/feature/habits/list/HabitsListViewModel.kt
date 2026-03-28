package com.example.habitflow.feature.habits.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.GetAllActiveHabitsUseCase
import com.example.habitflow.domain.usecase.GetEntriesForDateUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitsListViewModel @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val getEntriesForDateUseCase: GetEntriesForDateUseCase,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase,
) : ViewModel() {

    // Создаём Flow который будет эмитить даты.
    private val currentDate: Flow<LocalDate> = flow {
        //Бесконечный цикл — Flow живёт пока жив ViewModel.
        while (true) {
            //Эмитим текущую дату — первый раз сразу при старте, потом каждые 4 часа.
            emit(LocalDate.now())
            //Ждём 4 часа (4 часа × 60 минут × 60 секунд × 1000 мс).
            delay(4 * 60 * 60 * 1000L)
        }
    }.distinctUntilChanged()

    val state: StateFlow<HabitsListUiState> = combine(
        getAllActiveHabitsUseCase(),
        currentDate.flatMapLatest { date -> getEntriesForDateUseCase(date) }
    ) { habits, entries ->
        //сюда приходят ПОСЛЕДНИЕ значения из обоих flow
        // здесь ты просто ВОЗВРАЩАЕШЬ новое состояние
        when (habits) {
            is HabitResult.Success -> {
                if (habits.data.isEmpty()) HabitsListUiState.Empty
                else {
                    val habitsWithStatus = habits.data.map { habit ->
                        HabitWithStatus(
                            habit = habit,
                            isCompletedToday = entries.any { it.habitId == habit.id && it.isDone }
                        )
                    }
                    HabitsListUiState.Content(habitsWithStatus)
                }
            }

            is HabitResult.Error -> HabitsListUiState.Error(habits.message)
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
