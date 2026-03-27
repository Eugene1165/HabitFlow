package com.example.habitflow.feature.habits.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.domain.model.HabitResult
import com.example.habitflow.domain.usecase.AddHabitUseCase
import com.example.habitflow.domain.usecase.GetHabitByIdUseCase
import com.example.habitflow.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class HabitFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addHabitUseCase: AddHabitUseCase,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
) : ViewModel() {
    private val habitId: Int? = savedStateHandle.get<Int>("habitId")?.takeIf { it != -1 }
    private val _state = MutableStateFlow<HabitFormUiState>(HabitFormUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<HabitFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (habitId != null) {
            viewModelScope.launch {
                val habit = getHabitByIdUseCase.invoke(habitId)
                if (habit != null) {
                    _state.update {
                        HabitFormUiState(
                            title = habit.title,
                            description = habit.description ?: "",
                            color = habit.color,
                            repeatType = habit.repeatType,
                            startDate = habit.startDate,
                            target = habit.target,
                            reminder = habit.reminder,
                            isArchived = habit.isArchived
                        )
                    }
                } else {
                    _state.update { it.copy(error = "Привычка не найдена") }
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onColorChanged(color: String) {
        _state.update { it.copy(color = color) }
    }

    fun onRepeatTypeChanged(repeatType: RepeatType) {
        _state.update { it.copy(repeatType = repeatType) }
    }

    fun onSelectedDaysChanged(day: DayOfWeek) {
        val currentDays = (_state.value.repeatType as? RepeatType.WeeklyDays)?.days ?: emptyList()
        val newDays = if (day in currentDays) currentDays - day
        else currentDays + day
        _state.update { it.copy(repeatType = RepeatType.WeeklyDays(days = newDays)) }
    }

    fun onWeeklyCountChanged(count: Int) {
        _state.update { it.copy(repeatType = RepeatType.WeeklyCount(count = count)) }
    }

    fun onStartDateChanged(date: LocalDate) {
        _state.update { it.copy(startDate = date) }
    }

    fun onReminderChanged(time: LocalTime?) {
        _state.update { it.copy(reminder = time) }
    }

    fun onTargetChanged(target: Int?) {
        _state.update { it.copy(target = target) }
    }

    fun onSave() {
        if (_state.value.title.isBlank()) {
            _state.update { it.copy(error = "Заполните название привычки") }
            return
        }
        _state.update { it.copy(isSaving = true) }
        val current = _state.value
        if (habitId == null) {
            viewModelScope.launch {
                when (val result = addHabitUseCase.invoke(
                    Habit(
                        id = 0,
                        title = current.title,
                        description = current.description.ifBlank { null },
                        startDate = current.startDate,
                        color = current.color,
                        target = current.target,
                        isArchived = false,
                        repeatType = current.repeatType,
                        reminder = current.reminder
                    )
                )) {
                    is HabitResult.Success<*> -> {
                        _events.send(HabitFormEvent.NavigateToHabitsList)
                    }

                    is HabitResult.Error -> {
                        _state.update { it.copy(isSaving = false, error = result.message) }
                    }
                }
            }
        } else {
            viewModelScope.launch {
                when (val result = updateHabitUseCase.invoke(
                    Habit(
                        id = habitId,
                        title = current.title,
                        description = current.description.ifBlank { null },
                        startDate = current.startDate,
                        color = current.color,
                        target = current.target,
                        isArchived = current.isArchived,
                        repeatType = current.repeatType,
                        reminder = current.reminder
                    )
                )) {
                    is HabitResult.Success<*> -> {
                        _events.send(HabitFormEvent.NavigateToHabitFormInfo(habitId))
                    }

                    is HabitResult.Error -> {
                        _state.update { it.copy(isSaving = false, error = result.message) }
                    }
                }
            }
        }
    }


    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }
}
