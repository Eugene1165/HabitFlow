package com.example.habitflow.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun OnBoardingScreen(navController: NavController) {
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold() { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            //подписка на события
            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is OnBoardingEvent.NavigateToMain -> navController.navigate("main") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
            }

            when (state) {
                is OnBoardingUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is OnBoardingUiState.Content -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Добро пожаловать в HabitFlow")
                        Text(text = "Отслеживай привычки и достигай целей")
                        Button(onClick = {
                            viewModel.onComplete()
                        }) {
                            Text("Начать")
                        }
                    }
                }

                is OnBoardingUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ошибка загрузка")
                    }
                }
            }
        }
    }
}
