package com.example.habitflow.presentation.habits.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.R
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.presentation.components.HabitFlowTopBar
import java.time.DayOfWeek
import com.example.habitflow.presentation.extensions.toDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(navController: NavController) {
    val viewModel: CreateHabitViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

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
                title = stringResource(R.string.CreateHabbit),
                onBackClick = { navController.popBackStack() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box() {
            Column(modifier = Modifier.padding(paddingValues)) {
                TextField(
                    value = state.title,
                    onValueChange = { viewModel.onTitleChanged(it) },
                    label = { Text("Название") }
                )
                TextField(
                    value = state.description,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    label = { Text("Описание") }
                )
                TextField(
                    value = state.startDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Дата создания") }
                )
                TextField(
                    value = state.color,
                    onValueChange = { viewModel.onColorChanged(it) },
                    label = { Text("Цвет") }
                )
                TextField(
                    value = state.target?.toString() ?: "",
                    readOnly = false,
                    onValueChange = { viewModel.onTargetChanged(it.toIntOrNull()) },
                    label = { Text("Цель") }
                )
                ExposedDropdownMenuBox(
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
                TextField(
                    value = state.reminder?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Напоминание") }
                )
                Button(
                    onClick = { viewModel.onSave() },
                    enabled = !state.isSaving
                ) {
                    Text(if (state.isSaving) "Сохранение" else "Сохранить")
                }
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




