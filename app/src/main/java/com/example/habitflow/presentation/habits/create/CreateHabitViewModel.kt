package com.example.habitflow.presentation.habits.create

import androidx.lifecycle.ViewModel
import com.example.habitflow.domain.usecase.AddHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CreateHabitUiState>(CreateHabitUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<CreateHabitEvent>()
    val events = _events.receiveAsFlow()


    fun onSave(){

    }

}