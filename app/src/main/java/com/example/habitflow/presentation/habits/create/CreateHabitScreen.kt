package com.example.habitflow.presentation.habits.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.habitflow.R
import com.example.habitflow.presentation.components.HabitFlowTopBar
import com.example.habitflow.presentation.habits.form.HabitFormContent
import com.example.habitflow.presentation.habits.form.HabitFormEvent
import com.example.habitflow.presentation.habits.form.HabitFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(habitId: Int?, navController: NavController) {
    val viewModel: HabitFormViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                is HabitFormEvent.NavigateToHabitsList -> {
                    navController.popBackStack()
                }

                is HabitFormEvent.NavigateToHabitFormInfo -> {
                    navController.navigate("habit_info/${event.habitId}")
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HabitFormContent(
                state = state,
                modifier = Modifier,
                onTitleChanged = { viewModel.onTitleChanged(it) },
                onDescriptionChanged = { viewModel.onDescriptionChanged(it) },
                onColorChanged = { viewModel.onColorChanged(it) },
                onRepeatTypeChanged = { viewModel.onRepeatTypeChanged(it) },
                onSelectedDaysChanged = { viewModel.onSelectedDaysChanged(it) },
                onWeeklyCountChanged = { viewModel.onWeeklyCountChanged(it) },
                onTargetChanged = { viewModel.onTargetChanged(it) },
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








