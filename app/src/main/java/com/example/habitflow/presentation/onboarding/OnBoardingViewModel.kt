package com.example.habitflow.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecase.CompleteOnBoardingUseCase
import com.example.habitflow.domain.usecase.IsOnBoardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val isOnBoardingCompletedUseCase: IsOnBoardingCompletedUseCase,
    private val completeOnBoardingUseCase: CompleteOnBoardingUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<OnBoardingUiState>(OnBoardingUiState.Loading)
    val state = _state.asStateFlow()

    private val _events = Channel<OnBoardingEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val useCase = isOnBoardingCompletedUseCase.invoke().first()
            if (useCase) _events.send(OnBoardingEvent.NavigateToMain)
            else _state.value = OnBoardingUiState.Content
        }
    }

    fun onComplete() {
        viewModelScope.launch {
            completeOnBoardingUseCase.invoke()
            _events.send(OnBoardingEvent.NavigateToMain)
        }
    }
}