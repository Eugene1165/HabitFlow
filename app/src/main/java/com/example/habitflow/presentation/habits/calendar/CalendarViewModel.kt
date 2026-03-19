package com.example.habitflow.presentation.habits.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.GetHabitByIdUseCase
import com.example.habitflow.domain.usecase.GetHabitEntriesForPeriodUseCase
import com.example.habitflow.domain.usecase.ToggleHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val toggleHabitEntryUseCase: ToggleHabitEntryUseCase,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val getHabitEntriesForPeriodUseCase: GetHabitEntriesForPeriodUseCase
) : ViewModel() {

    private val habitId: Int = checkNotNull(savedStateHandle["habitId"])

    private val _yearMonth = MutableStateFlow<YearMonth>(YearMonth.now())


    private val _events = Channel<CalendarEvent>()
    val events = _events.receiveAsFlow()

    private val habitFlow = flow { emit(getHabitByIdUseCase(habitId)) }

    val state: StateFlow<CalendarUiState> = _yearMonth
        .flatMapLatest { month ->
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()
            getHabitEntriesForPeriodUseCase(habitId, startDate, endDate)
                .map { entries -> month to entries }
        }
        .combine(habitFlow) { (month, entries), habit ->
            if (habit == null) {
                CalendarUiState.Error("Ошибка загрузки привычки")
            } else {
                val doneDates = entries.filter { it.isDone }.map { it.date }.toSet()
                CalendarUiState.Content(
                    habit = habit,
                    yearMonth = month,
                    doneDates = doneDates
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIBE_TIMEOUT_MS),
            initialValue = CalendarUiState.Loading
        )


    fun onDateToggled(date: LocalDate) {
        viewModelScope.launch {
            try {
                toggleHabitEntryUseCase.invoke(habitId, date)
            } catch (e: IllegalArgumentException) {
                _events.send(CalendarEvent.ShowError(e.message ?: "Ошибка выполнения операции"))
            }
        }
    }

    fun onMonthChanged(yearMonth: YearMonth) {
        _yearMonth.value = yearMonth
    }

    fun onNavigateBack() {
        viewModelScope.launch { _events.send(CalendarEvent.NavigateBack) }
    }

    companion object{
        private const val SUBSCRIBE_TIMEOUT_MS = 5000L
    }
}
