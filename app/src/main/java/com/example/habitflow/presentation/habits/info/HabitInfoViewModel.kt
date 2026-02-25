package com.example.habitflow.presentation.habits.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.ArchiveHabitUseCase
import com.example.habitflow.domain.usecase.GetHabitByIdUseCase
import com.example.habitflow.domain.usecase.GetHabitEntriesForPeriodUseCase
import com.example.habitflow.domain.usecase.GetHabitsStatisticsUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val getHabitsStatisticsUseCase: GetHabitsStatisticsUseCase,
    private val getHabitEntriesForPeriodUseCase: GetHabitEntriesForPeriodUseCase,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase,
    private val archiveHabitUseCase: ArchiveHabitUseCase,
) : ViewModel() {
    private val habitId: Int = checkNotNull(savedStateHandle["habitId"])
    private val _state = MutableStateFlow<HabitInfoUiState>(HabitInfoUiState.Loading)
    val state = _state.asStateFlow()
    private val _events = Channel<HabitInfoEvent>()
    val events = _events.receiveAsFlow()


    init {
        loadHabit()
    }

    private var loadJob: Job? = null
    fun loadHabit() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = HabitInfoUiState.Loading
            val habit = getHabitByIdUseCase.invoke(habitId)
            if (habit == null) {
                _state.value = HabitInfoUiState.Error(message = "Ошибка загрузки")
                return@launch
            }
            val today = LocalDate.now()
            val weekStart = today.minusDays(6)

            combine(
                getHabitsStatisticsUseCase(habitId),
                getHabitEntriesForPeriodUseCase(habitId, weekStart, today)
            ) { stats, entries ->
                HabitInfoUiState.Content(
                    habit,
                    stats,
                    entries.any { it.date == today && it.isDone },
                    entries
                )
            }
                .catch { e -> _state.value = HabitInfoUiState.Error(e.message ?: "Ошибка") }
                .collect { _state.value = it }
        }
    }

    fun onToggleToday() {
        val date = LocalDate.now()
        viewModelScope.launch {
            try {
                toggleHabitEntryUseCase.invoke(habitId, date)
            } catch (t: Throwable) {
                _state.value = HabitInfoUiState.Error(t.message ?: "Ошибка выполнения операции")
            }
        }
    }

    fun onArchive() {
        viewModelScope.launch {
            try {
                archiveHabitUseCase.invoke(habitId)
                _events.send(HabitInfoEvent.NavigateBack)
            } catch (t: Throwable) {
                _state.value = HabitInfoUiState.Error(t.message ?: "Ошибка выполнения операции")
            }
        }
    }

    fun onNavigateToCalendar() {
        viewModelScope.launch {
            _events.send(HabitInfoEvent.NavigateToCalendar(habitId))
        }
    }
}
