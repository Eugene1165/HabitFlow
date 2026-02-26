package com.example.habitflow.presentation.habits.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.R
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.presentation.components.HabitFlowTopBar
import com.example.habitflow.presentation.extensions.toDisplayName
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(navController: NavController) {
    val viewModel: CreateHabitViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateHabitEvent.NavigateToHabitsList -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            HabitFlowTopBar(
                title = stringResource(R.string.createHabbit),
                onBackClick = { navController.popBackStack() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Название") }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text("Описание") }
            )
            Spacer(Modifier.height(8.dp))
            Text("Цвет", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            Spacer(Modifier.height(8.dp))
            SetColor(
                selectedColor = state.color,
                colors = listOf(
                    "#F44336",
                    "#FF9800",
                    "#FFEB3B",
                    "#4CAF50",
                    "#2196F3",
                    "#9C27B0",
                    "#E91E63",
                    "#607D8B"
                ),
                onColorSelected = { viewModel.onColorChanged(it) }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.target?.toString() ?: "",
                readOnly = false,
                onValueChange = { viewModel.onTargetChanged(it.toIntOrNull()) },
                label = { Text("Цель") }
            )
            Spacer(Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    value = state.repeatType.toDisplayName(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Ежедневно") },
                        onClick = {
                            viewModel.onRepeatTypeChanged(RepeatType.Daily)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Количество дней") },
                        onClick = {
                            viewModel.onRepeatTypeChanged(RepeatType.WeeklyDays(days = emptyList()))
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Количество раз") },
                        onClick = {
                            viewModel.onRepeatTypeChanged(RepeatType.WeeklyCount(count = 1))
                            expanded = false
                        }
                    )
                }
            }
            when (state.repeatType) {
                is RepeatType.WeeklyDays -> WeeklyDaysSelector(
                    selectedDays = (state.repeatType as RepeatType.WeeklyDays).days,
                    onDayToggle = { viewModel.onSelectedDaysChanged(it) }
                )

                is RepeatType.WeeklyCount -> TextField(
                    value = (state.repeatType as RepeatType.WeeklyCount).count.toString(),
                    onValueChange = { viewModel.onWeeklyCountChanged(it.toIntOrNull() ?: 1) },
                    label = { Text("Раз в неделю") }
                )

                RepeatType.Daily -> {}
            }
            Spacer(Modifier.height(8.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.reminder?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Напоминание") }
            )
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { viewModel.onSave() },
                enabled = !state.isSaving
            ) {
                Text(if (state.isSaving) "Сохранение" else "Сохранить")
            }
        }

    }
}

@Composable
private fun WeeklyDaysSelector(
    selectedDays: List<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
    FlowRow() {
        DayOfWeek.entries.forEach { day ->
            FilterChip(
                selected = day in selectedDays,
                onClick = { onDayToggle(day) },
                label = { Text(day.toDisplayName()) }
            )
        }
    }
}

@Composable
private fun SetColor(
    selectedColor: String,
    colors: List<String>,
    onColorSelected: (String) -> Unit
) {
    FlowRow {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color.toColorInt()))
                    .then(
                        if (color == selectedColor)
                            Modifier.border(2.dp, Color.Black, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorSelected(color) }
            ) { }
        }
    }
}




