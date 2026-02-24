package com.example.habitflow.presentation.habits.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun HabitsListScreen(navController: NavController) {
    val viewModel: HabitsListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { it.toString()
        Box(
            modifier = Modifier.fillMaxSize()
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Нет привычек")
                    }

                }

                is HabitsListUiState.Content -> {
                    val habits = (state as HabitsListUiState.Content).habits
                    LazyColumn {
                        items(habits) { habitWithStatus ->
                            HabitItem(
                                habit = habitWithStatus.habit,
                                isCompletedToday = habitWithStatus.isCompletedToday,
                                onToggle = { habitId -> viewModel.onToggle(habitId) },
                                onCardClick = { habitId -> navController.navigate("habit_info/$habitId") }
                            )
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
                            Button(onClick = {}) {
                                Text("Повторить")
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color.Green,
                onClick = { navController.navigate("create_habit") }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}


@Composable
fun HabitItem(
    habit: Habit,
    isCompletedToday: Boolean,
    onToggle: (habitId: Int) -> Unit,
    onCardClick: (habitId: Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onCardClick(habit.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(habit.color.toColorInt()))
            ) {}
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = habit.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = habit.repeatType.toString(), color = Color.Gray)
            }
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { onToggle(habit.id) })
        }
    }

}


@Preview
@Composable
fun PreviewHabitItem() {
    val fakeHabit = Habit(
        id = 1,
        title = "Утренняя зарядка",
        description = "Каждое утро",
        startDate = LocalDate.now(),
        color = "#FF5733",
        target = null,
        isArchived = false,
        repeatType = RepeatType.Daily,
        reminder = LocalTime.of(7, 0)
    )
    HabitItem(
        habit = fakeHabit,
        isCompletedToday = true,
        onToggle = {},
        onCardClick = {}
    )
}