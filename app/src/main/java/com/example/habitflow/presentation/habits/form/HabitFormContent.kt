package com.example.habitflow.presentation.habits.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.presentation.components.ColorPicker
import com.example.habitflow.presentation.components.habitColors
import com.example.habitflow.presentation.extensions.toDisplayName
import java.time.DayOfWeek
import java.time.LocalTime

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
    onReminderChanged: (LocalTime?) -> Unit
) {
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .testTag("screen_habit_form")
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .testTag("habit_name")
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.title,
            onValueChange = { onTitleChanged(it) },
            label = { Text("Название") }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            modifier = Modifier
                .testTag("habit_description")
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.description,
            onValueChange = { onDescriptionChanged(it) },
            label = { Text("Описание") }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Цвет",
            modifier = Modifier
                .testTag("habit_color")
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(Modifier.height(8.dp))
        ColorPicker(
            selectedColor = state.color,
            colors = habitColors,
            onColorSelected = { onColorChanged(it) }
        )
        Spacer(Modifier.height(8.dp))
        var targetText by remember { mutableStateOf(state.target?.toString() ?: "") }
        TextField(
            modifier = Modifier
                .testTag("target")
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = targetText,
            onValueChange = { newText ->
                targetText = newText
                onTargetChanged(newText.toIntOrNull())
            },
            label = { Text("Цель") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            modifier = Modifier
                .testTag("dropdown_menu")
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
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
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

            is RepeatType.WeeklyCount -> {
                var countText by remember { mutableStateOf(state.repeatType.count.toString()) }
                TextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = countText,
                    onValueChange = { newText ->
                        countText = newText
                        newText.toIntOrNull()?.let { text ->
                            if (text == 0) {
                                onWeeklyCountChanged(1)
                                countText = "1"
                            } else onWeeklyCountChanged(text)
                        }
                    },
                    label = { Text("Раз в неделю") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            RepeatType.Daily -> {}
        }
        Spacer(Modifier.height(8.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    showTimePicker = true
                },
            value = state.reminder?.toString() ?: "",
            enabled = false, // поле не получало фокус а клик обрабатывался через clickable
            onValueChange = {},
            label = { Text("Напоминание") }
        )
        if (showTimePicker) {
            TimePickerInRemember(
                initialTime = state.reminder,
                onConfirm = { time ->
                    onReminderChanged(time)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false })
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerInRemember(
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    initialTime: LocalTime?
) {
    val timePicker = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 0,
        initialMinute = initialTime?.minute ?: 0
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = { TimePicker(state = timePicker) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        LocalTime.of(
                            timePicker.hour,
                            timePicker.minute
                        )
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отменить") }
        }
    )
}
