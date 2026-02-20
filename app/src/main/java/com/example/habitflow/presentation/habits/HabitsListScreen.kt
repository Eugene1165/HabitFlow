package com.example.habitflow.presentation.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow.domain.model.Habit
import androidx.core.graphics.toColorInt


@Composable
fun HabitsListScreen() {
    val viewModel: HabitsListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {}
    ) { _ ->
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
                    items(habits) { habit ->
                        HabitItem(habit)
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
    }
}


@Composable
fun HabitItem(
    habit: Habit,
    isCompletedToday: Boolean,
    onToggle: (habitId: Int) -> Unit,
    onCardClick: (habitId: Int) -> Unit,
) {
    Row(modifier = Modifier) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color(habit.color.toColorInt()))
        ) {
            Column() {
                Text(habit.title)
                Text(habit.repeatType.toString())
            }
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { onToggle(habit.id) })
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewHabitItem(){
    val fakeHabit = Habit(
        id = 1,
        title = "Утренняя зарядка",
        description = "Каждое утро",
        startDate = java.time.LocalDate.now(),
        color = "#FF5733",
        target = null,
        isArchived = false,
        repeatType = com.example.habitflow.domain.model.RepeatType.Daily,
        reminder = java.time.LocalTime.of(7, 0)
    )
    HabitItem(
        habit = fakeHabit,
        isCompletedToday = true,
        onToggle = {},
        onCardClick = {}
    )
}