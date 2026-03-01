package com.example.habitflow.presentation.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow.presentation.components.HabitFlowTopBar
import com.example.habitflow.presentation.extensions.toDisplayName
import java.time.DayOfWeek

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            HabitFlowTopBar(
                title = "Настройки",
                onBackClick = {}
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is SettingsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SettingsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ошибка загрузки настроек")
                        }
                    }
                }

                is SettingsUiState.Content -> {
                    val darkTheme = (state as SettingsUiState.Content).isDarkTheme
                    val firstDay = (state as SettingsUiState.Content).firstDayOfWeek
                    SettingsContent(
                        isDarkTheme = darkTheme,
                        firstDayOfWeek = firstDay,
                        onDarkThemeToggled = viewModel::onDarkThemeToggled,
                        onFirstDayChanged = viewModel::onFirstDayOfWeekChanged
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    firstDayOfWeek: DayOfWeek,
    onDarkThemeToggled: (Boolean) -> Unit,
    onFirstDayChanged: (DayOfWeek) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppearanceSection(
            isDarkTheme = isDarkTheme,
            onDarkThemeToggled = onDarkThemeToggled
        )
        CalendarSection(
            firstDayOfWeek = firstDayOfWeek,
            onFirstDayChanged = onFirstDayChanged
        )
    }
}

@Composable
fun AppearanceSection(
    isDarkTheme: Boolean,
    onDarkThemeToggled: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Темная тема")
        Switch(checked = isDarkTheme, onCheckedChange = onDarkThemeToggled)
    }
}

@Composable
fun CalendarSection(
    firstDayOfWeek: DayOfWeek,
    onFirstDayChanged: (DayOfWeek) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DayOfWeek.entries.forEach { day ->
            FilterChip(
                selected = day == firstDayOfWeek,
                onClick = { onFirstDayChanged(day) },
                label = { Text(day.toDisplayName()) }
            )
        }
    }
}
