package com.example.habitflow.feature.habits.form

import com.example.habitflow.domain.model.RepeatType
import java.time.DayOfWeek
import java.time.LocalTime

data class HabitFormCallbacks (
    val onTitleChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onColorChanged: (String) -> Unit,
    val onRepeatTypeChanged: (RepeatType) -> Unit,
    val onSelectedDaysChanged: (DayOfWeek) -> Unit,
    val onWeeklyCountChanged: (Int) -> Unit,
    val onTargetChanged: (Int?) -> Unit,
    val onReminderChanged: (LocalTime?) -> Unit
)
