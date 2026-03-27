package com.example.habitflow.feature.statistics

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow.domain.model.AllHabitsStatistics

private const val PERCENT_MULTIPLIER = 100

@Composable
fun StatisticsScreen() {
    val viewModel: StatisticsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.Companion
            .testTag("screen_statistics")
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (state) {
                is StatisticsUiState.Loading -> {
                    Box(
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is StatisticsUiState.Error -> {
                    Box(
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                            Text("Ошибка получения статистики по всем привычкам")
                        }
                    }
                }

                is StatisticsUiState.Content -> {
                    val statisticsData = (state as StatisticsUiState.Content).data
                    StatisticsContent(statisticsData)

                }

                is StatisticsUiState.Empty -> {
                    Column(
                        modifier = Modifier.Companion.fillMaxSize(),
                        horizontalAlignment = Alignment.Companion.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📋", fontSize = 48.sp)
                        Spacer(Modifier.Companion.height(8.dp))
                        Text(
                            "Нет cтатистики по привычкам",
                            fontWeight = FontWeight.Companion.SemiBold
                        )
                        Text(
                            "Отмечайте привычки для сбора статистики",
                            color = Color.Companion.Gray
                        )
                    }

                }
            }
        }

    }
}

@Composable
fun StatisticsContent(data: AllHabitsStatistics) {
    Column(
        modifier = Modifier.Companion.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActiveHabitsCard(count = data.activeHabitsCount)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StreakCard(
                modifier = Modifier.Companion.weight(1f),
                label = "Best streak",
                streakValue = data.bestStreak.second,
                habitName = data.bestStreak.first.title
            )

            StreakCard(
                modifier = Modifier.Companion.weight(1f),
                label = "Current streak",
                streakValue = data.currentStreak.second,
                habitName = data.currentStreak.first.title
            )
        }

        MostConsistentCard(
            habitName = data.mostConsistent.first.title,
            percent = data.mostConsistent.second
        )

    }

}

@Composable
fun ActiveHabitsCard(count: Int) {
    Card(
        modifier = Modifier.Companion
            .testTag("active_habit_card")
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.Companion.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Text("$count", fontWeight = FontWeight.Companion.Bold, fontSize = 28.sp)
            Text("активных привычек", color = Color.Companion.Gray)
        }
    }
}

@Composable
fun StreakCard(modifier: Modifier, label: String, streakValue: Int, habitName: String) {
    Card(
        modifier = modifier.testTag("streak_habit_card"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.Companion.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label)
            Text("$streakValue дн.", fontSize = 28.sp, fontWeight = FontWeight.Companion.Bold)
            Text(habitName, color = Color.Companion.Gray, maxLines = 1)
        }
    }
}

@Composable
fun MostConsistentCard(habitName: String, percent: Float) {
    Card(
        modifier = Modifier.Companion
            .testTag("most_habit_card")
            .fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.Companion.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cамая стабильная")
            Text(habitName, fontWeight = FontWeight.Companion.Bold)
            LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier.Companion.fillMaxWidth()
            )
            Text("${(percent * PERCENT_MULTIPLIER).toInt()}%")

        }
    }
}
