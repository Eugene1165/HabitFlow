package com.example.habitflow.presentation.habits.create

import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun CreateHabitScreen(navController: NavController){
    val viewModel: CreateHabitViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(

    ) { }

}