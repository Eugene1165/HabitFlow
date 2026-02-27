package com.example.habitflow.presentation.habits.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.usecase.ArchiveHabitUseCase
import com.example.habitflow.domain.usecase.GetHabitEntriesForPeriodUseCase
import com.example.habitflow.domain.usecase.GetHabitsStatisticsUseCase
import com.example.habitflow.domain.usecase.ObserveHabitByIdUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import com.example.habitflow.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HabitInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitsStatisticsUseCase: GetHabitsStatisticsUseCase,
    private val getHabitEntriesForPeriodUseCase: GetHabitEntriesForPeriodUseCase,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase,
    private val archiveHabitUseCase: ArchiveHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val observeHabitByIdUseCase: ObserveHabitByIdUseCase
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
            val today = LocalDate.now()
            val weekStart = today.minusDays(6)

            combine(
                getHabitsStatisticsUseCase(habitId),
                getHabitEntriesForPeriodUseCase(habitId, weekStart, today),
                observeHabitByIdUseCase(habitId)
            ) { stats, entries, habit ->
                if (habit == null) {
                    HabitInfoUiState.Error(message = "Ошибка загрузки")
                } else {
                    HabitInfoUiState.Content(
                        habit,
                        stats,
                        entries.any { it.date == today && it.isDone },
                        entries,
                        null
                    )
                }
            }
                .catch { e -> _state.value = HabitInfoUiState.Error(e.message ?: "Ошибка") }
                .collect { newContent ->
                    when (newContent) {
                        is HabitInfoUiState.Error -> {
                            _state.value = newContent
                        }

                        is HabitInfoUiState.Content -> {
                            val currentEditing =
                                (_state.value as? HabitInfoUiState.Content)?.editingHabit
                            _state.value = newContent.copy(editingHabit = currentEditing)
                        }

                        else -> {}
                    }
                }
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

    fun onStartEditing() {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        val newHabit = content.habit.copy()
        _state.update { content.copy(editingHabit = newHabit) }
    }

    fun onCancelEditing() {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        _state.update { content.copy(editingHabit = null) }

    }

    fun onSaveHabit() {
        viewModelScope.launch {
            try {
                val content = _state.value as? HabitInfoUiState.Content
                val newHabit = content?.editingHabit?.copy() ?: return@launch
                updateHabitUseCase.invoke(newHabit)
                _state.update { currentState ->
                    (currentState as? HabitInfoUiState.Content)?.copy(
                        habit = newHabit,
                        editingHabit = null
                    ) ?: currentState
                }
            } catch (t: Throwable) {
                _state.value =
                    HabitInfoUiState.Error(t.message ?: "Ошибка обновления параметров привычки")
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        val updated = content.editingHabit?.copy(title = newTitle) ?: return
        _state.update { content.copy(editingHabit = updated) }

    }

    fun onColorChanged(newColor: String) {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        val updated = content.editingHabit?.copy(color = newColor) ?: return
        _state.update { content.copy(editingHabit = updated) }

    }

    fun onRepeatTypeChanged(newRepeatType: RepeatType) {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        val updated = content.editingHabit?.copy(repeatType = newRepeatType) ?: return
        _state.update { content.copy(editingHabit = updated) }
    }

    fun onReminderChanged(newReminder: LocalTime?) {
        val content = _state.value as? HabitInfoUiState.Content ?: return
        val updated = content.editingHabit?.copy(reminder = newReminder) ?: return
        _state.update { content.copy(editingHabit = updated) }
    }


}
