package com.example.habitflow.presentation.habits.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.ui.components.HabitFlowTopBar
import java.time.LocalDate

private const val PERCENT_MULTIPLIER = 100
private const val WEEK_DAYS_BACK = 6

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
        modifier = Modifier.testTag("screen_habit_info"),
        topBar = {
            HabitFlowTopBar(
                title = "Привычка №$habitId",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            if (state is HabitInfoUiState.Content) {
                FloatingActionButton(onClick = { navController.navigate("create_habit?habitId=$habitId") }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
        }
    ) {paddingValues ->
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
                HabitInfoContent(
                    paddingValues = paddingValues ,
                    state = currentState,
                    onToggleToday = { viewModel.onToggleToday() },
                    onNavigateToCalendar = { viewModel.onNavigateToCalendar() },
                    onArchive = { viewModel.onArchive() },
                )
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
fun StatisticCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    unit: String
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null)
            Text(title, color = Color.Gray, fontSize = 11.sp)
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(unit, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
fun HabitInfoContent(
    paddingValues: PaddingValues,
    state: HabitInfoUiState.Content,
    onToggleToday: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onArchive: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(state.habit.color.toColorInt()))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = state.habit.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = state.habit.description ?: "",
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
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                title = "Серия",
                value = "${state.statistics.currentStreak}",
                unit = "дней"
            )
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                title = "Лучшая серия",
                value = "${state.statistics.bestStreak}",
                unit = "дней"
            )
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Info,
                title = "Процент",
                value = "%.0f%%".format(
                    state.statistics.percentCompletion * PERCENT_MULTIPLIER
                ),
                unit = "выполнения"
            )
        }
        Spacer(Modifier.height(16.dp))
        WeeklyProgressRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            weeklyEntries = state.weeklyEntries
        )
        Spacer(Modifier.height(16.dp))
        ButtonsBlock(
            habitColor = state.habit.color,
            isTodayDone = state.isTodayDone,
            isArchived = state.habit.isArchived,
            onToggleToday = { onToggleToday() },
            onNavigateToCalendar = { onNavigateToCalendar() },
            onArchive = { onArchive() }
        )
    }
}

@Suppress("LongParameterList")
@Composable
fun ButtonsBlock(
    habitColor: String,
    isTodayDone: Boolean,
    isArchived: Boolean,
    onToggleToday: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onArchive: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = { onToggleToday() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(habitColor.toColorInt())
        )
    ) {
        Text(if (isTodayDone) "Снять отметку" else "Отметить сегодня")
    }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("btn_open_calendar"),
        onClick = { onNavigateToCalendar() }
    ) {
        Text("Открыть календарь")
    }
    //архивируем привычку-кнопка для архивации
    if (!isArchived) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("btn_archive_habit"),
            onClick = { onArchive() }
        ) {
            Text("Архивировать привычку")
        }
    }
}

@Composable
fun WeeklyProgressRow(modifier: Modifier, weeklyEntries: List<HabitEntry>) {
    val today = LocalDate.now()
    val days = (WEEK_DAYS_BACK downTo 0).map { today.minusDays(it.toLong()) }
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
