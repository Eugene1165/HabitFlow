package com.example.habitflow.presentation.habits.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.presentation.components.HabitFlowTopBar
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun CalendarScreen(habitId: Int, navController: NavController) {

    val viewModel: CalendarViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CalendarEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            HabitFlowTopBar(
                title = (state as? CalendarUiState.Content)?.habit?.title ?: "",
                onBackClick = { viewModel.onNavigateBack() }
            )
        }
    ) { paddingValues ->

        when (val currentState = state) {
            is CalendarUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CalendarUiState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onMonthChanged(
                                    yearMonth = currentState.yearMonth.minusMonths(1)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                        Text(
                            currentState.yearMonth.format(
                                DateTimeFormatter
                                    .ofPattern("MMMM yyyy", Locale.forLanguageTag("ru"))
                            )
                        )
                        IconButton(
                            onClick = {
                                viewModel.onMonthChanged(
                                    yearMonth = currentState.yearMonth.plusMonths(1)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                    CalendarContent(
                        yearMonth = currentState.yearMonth,
                        habit = currentState.habit,
                        doneDates = currentState.doneDates,
                        onDayClick = { date -> viewModel.onDateToggled(date) },
                        onMonthChanged = { yearMonth -> viewModel.onMonthChanged(yearMonth) }
                    )
                }
            }

            is CalendarUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка загрузки календаря")
                    }
                }
            }
        }
    }

}

@Composable
fun DayCell(
    day: CalendarDay,
    doneDates: Set<LocalDate>,
    habitColor: Color,
    startDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()

    val isFuture = day.date.isAfter(today)
    val isBeforeStart = day.date.isBefore(startDate)
    val isDone = day.date in doneDates
    val missedDay = day.date.isBefore(today) && !day.date.isBefore(startDate) && !isDone
    val isOutDate = day.position != DayPosition.MonthDate
    val isClickable = !isFuture && !isBeforeStart && !isOutDate
    val backgroundColor = when {
        isOutDate -> Color.Transparent
        isDone -> habitColor
        missedDay -> habitColor.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = isClickable) { onDayClick(day.date) }
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (!isDone) Color.Gray else Color.White
        )
    }
}

@Composable
fun CalendarContent(
    yearMonth: YearMonth,
    habit: Habit,
    doneDates: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.from(habit.startDate),
        endMonth = YearMonth.now(),
        firstVisibleMonth = yearMonth
    )

    var isFirstRender by remember { mutableStateOf(true) }

    LaunchedEffect(calendarState.firstVisibleMonth.yearMonth) {
        if (isFirstRender) {
            isFirstRender = false
            return@LaunchedEffect
        }
        onMonthChanged(calendarState.firstVisibleMonth.yearMonth)
    }

    LaunchedEffect(yearMonth) {
        calendarState.animateScrollToMonth(yearMonth)
    }

    HorizontalCalendar(
        state = calendarState,
        dayContent = { day ->
            DayCell(
                day = day,
                doneDates = doneDates,
                habitColor = Color(habit.color.toColorInt()),
                startDate = habit.startDate,
                onDayClick = onDayClick
            )
        }
    )
}