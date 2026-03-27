package com.example.habitflow.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnBoardingScreen(navController: NavController) {
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //подписка на события
            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is OnBoardingEvent.NavigateToMain -> navController.navigate(route = "main") {
                            popUpTo(route = "onboarding") { inclusive = true }
                        }
                    }
                }
            }

            when (state) {
                is OnBoardingUiState.Loading -> {
                    Box(
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is OnBoardingUiState.Content -> {
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .background(
                                Brush.Companion.verticalGradient(
                                    colors = listOf(
                                        Color(color = 0xFF6650A4),
                                        Color(color = 0xFF1A1A2E)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier.Companion.fillMaxSize(),
                            horizontalAlignment = Alignment.Companion.CenterHorizontally,
                        ) {
                            OnBoardingHeader()
                            OnBoardingContent()
                            Spacer(Modifier.Companion.weight(weight = 1f))
                            Button(
                                modifier = Modifier.Companion.fillMaxWidth(fraction = 0.8f),
                                onClick = { viewModel.onComplete() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Companion.White)
                            ) {
                                Text(text = "Начать ", color = Color(color = 0xFF6650A4))
                            }
                            Spacer(modifier = Modifier.Companion.height(height = 32.dp))
                        }
                    }
                }

                is OnBoardingUiState.Error -> {
                    Box(
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Text("Ошибка загрузка")
                    }
                }
            }
        }
    }
}

@Composable
fun OnBoardingHeader(){
    Spacer(modifier = Modifier.Companion.height(height = 64.dp))
    Text(text = "🎯", fontSize = 80.sp)
    Spacer(Modifier.Companion.height(height = 16.dp))
    Text(
        text = "HabitFlow",
        fontSize = 36.sp,
        fontWeight = FontWeight.Companion.Bold,
        color = Color.Companion.White
    )
    Spacer(Modifier.Companion.height(height = 8.dp))
    Text(
        text = "Формируй привычки,\nменяй жизнь",
        fontSize = 16.sp,
        color = Color.Companion.White.copy(alpha = 0.7f),
        textAlign = TextAlign.Companion.Center
    )
    Spacer(Modifier.Companion.height(height = 32.dp))
}

@Composable
fun OnBoardingContent(){
    Row(
        modifier = Modifier.Companion.fillMaxWidth(fraction = 0.8f),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.Companion.White,
            modifier = Modifier.Companion.size(size = 20.dp)
        )
        Spacer(Modifier.Companion.width(width = 8.dp))
        Text("Отслеживай каждый день", color = Color.Companion.White)
    }
    Spacer(Modifier.Companion.height(height = 12.dp))
    Row(
        modifier = Modifier.Companion.fillMaxWidth(fraction = 0.8f),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color.Companion.White,
            modifier = Modifier.Companion.size(size = 20.dp)
        )
        Spacer(Modifier.Companion.width(width = 8.dp))
        Text(text = "Смотри статистику ", color = Color.Companion.White)
    }
    Spacer(Modifier.Companion.height(height = 12.dp))
    Row(
        modifier = Modifier.Companion.fillMaxWidth(fraction = 0.8f),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = Color.Companion.White,
            modifier = Modifier.Companion.size(size = 20.dp)
        )
        Spacer(Modifier.Companion.width(width = 8.dp))
        Text(text = "Не забывай с напоминаниями", color = Color.Companion.White)
    }
}
