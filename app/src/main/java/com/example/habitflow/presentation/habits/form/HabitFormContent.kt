package com.example.habitflow.presentation.habits.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.presentation.components.ColorPicker
import com.example.habitflow.presentation.components.habitColors
import com.example.habitflow.presentation.extensions.toDisplayName
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormContent(
    state: HabitFormUiState,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onColorChanged: (String) -> Unit,
    onRepeatTypeChanged: (RepeatType) -> Unit,
    onSelectedDaysChanged: (DayOfWeek) -> Unit,
    onWeeklyCountChanged: (Int) -> Unit,
    onTargetChanged: (Int?) -> Unit,
) {
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.title,
            onValueChange = { onTitleChanged(it) },
            label = { Text("Название") }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.description,
            onValueChange = { onDescriptionChanged(it) },
            label = { Text("Описание") }
        )
        Spacer(Modifier.height(8.dp))
        Text("Цвет", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Spacer(Modifier.height(8.dp))
        ColorPicker(
            selectedColor = state.color,
            colors = habitColors,
            onColorSelected = { onColorChanged(it) }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.target?.toString() ?: "",
            readOnly = false,
            onValueChange = { onTargetChanged(it.toIntOrNull()) },
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
                        onRepeatTypeChanged(RepeatType.Daily)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Количество дней") },
                    onClick = {
                        onRepeatTypeChanged(RepeatType.WeeklyDays(days = emptyList()))
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Количество раз") },
                    onClick = {
                        onRepeatTypeChanged(RepeatType.WeeklyCount(count = 1))
                        expanded = false
                    }
                )
            }
        }
        when (state.repeatType) {
            is RepeatType.WeeklyDays -> WeeklyDaysSelector(
                selectedDays = state.repeatType.days,
                onDayToggle = { onSelectedDaysChanged(it) }
            )

            is RepeatType.WeeklyCount -> TextField(
                value = state.repeatType.count.toString(),
                onValueChange = { onWeeklyCountChanged(it.toIntOrNull() ?: 1) },
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
    }
}

@Composable
fun WeeklyDaysSelector(
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
