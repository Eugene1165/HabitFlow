package com.example.habitflow.presentation.habits.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.presentation.components.HabitFlowTopBar
import java.time.LocalDate

@Composable
fun HabitInfoScreen(habitId: Int, navController: NavController) {
    val viewModel: HabitInfoViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HabitInfoEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is HabitInfoEvent.NavigateToCalendar -> {
                    navController.navigate("calendar/${event.habitId}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            HabitFlowTopBar(
                title = "Привычка №$habitId",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            is HabitInfoUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HabitInfoUiState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(android.graphics.Color.parseColor(currentState.habit.color)))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = currentState.habit.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Text(
                                text = currentState.habit.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null)
                                Text(text = "Streak", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    text = "${currentState.statistics.currentStreak}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("дней", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Card(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null)
                                Text("best streak", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    text = "${currentState.statistics.bestStreak}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("дней", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Card(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null)
                                Text("Процент", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    text = "%.0f%%".format(
                                        currentState.statistics.percentCompletion * 100
                                    ),
                                    fontWeight = FontWeight.Bold
                                )
                                Text("выполнения", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    WeeklyProgressRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        weeklyEntries = currentState.weeklyEntries)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { viewModel.onToggleToday() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(currentState.habit.color.toColorInt())
                        )
                    ) {
                        Text(if (currentState.isTodayDone) "Снять отметку" else "Отметить сегодня")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { viewModel.onNavigateToCalendar() }) {
                        Text("Открыть календарь")
                    }
                }
            }

            is HabitInfoUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка загрузки привычки")
                        Button(onClick = { viewModel.loadHabit() }) {
                            Text("Повторить")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun WeeklyProgressRow(modifier: Modifier,weeklyEntries: List<HabitEntry>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val daysNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { day ->
            val entry = weeklyEntries.find { it.date == day }
            val color = when {
                entry?.isDone == true -> MaterialTheme.colorScheme.primary
                entry != null -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = daysNames[day.dayOfWeek.value - 1],
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}