package com.example.habitflow.feature.habits.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.ui.components.HabitCard


@Composable
fun HabitsListScreen(navController: NavController) {
    val viewModel: HabitsListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier
            .testTag("habit_list_screen")
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),

            ) {
            when (state) {
                is HabitsListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }

                is HabitsListUiState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📋", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Нет привычек", fontWeight = FontWeight.SemiBold)
                        Text("Создай первую привычку", color = Color.Gray)
                    }
                }

                is HabitsListUiState.Content -> {
                    val habits = (state as HabitsListUiState.Content).habits
                    LazyColumn(
                        modifier = Modifier.testTag("habits_list"),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(habits, key = {it.habit.id}) { habitWithStatus ->
                            HabitCard(
                                habit = habitWithStatus.habit,
                                onClick = { habitId -> navController.navigate("habit_info/$habitId") }
                            ) {
                                Checkbox(
                                    modifier = Modifier.testTag("checkbox_habit_${habitWithStatus.habit.id}"),
                                    checked = habitWithStatus.isCompletedToday,
                                    onCheckedChange = { viewModel.onToggle(habitId = habitWithStatus.habit.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(habitWithStatus.habit.color.toColorInt()),
                                        uncheckedColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }

                is HabitsListUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ошибка")
                        }
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .testTag("fab_add_new_habit")
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(color = 0xFF6650A4),
                onClick = { navController.navigate("create_habit") }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}
