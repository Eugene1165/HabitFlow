package com.example.habitflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitflow.presentation.habits.CreateHabitScreen
import com.example.habitflow.presentation.habits.HabitInfoScreen
import com.example.habitflow.presentation.screens.CalendarScreen
import com.example.habitflow.presentation.screens.MainScreen
import com.example.habitflow.presentation.screens.OnBoardingScreen

@Composable
fun HostNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnBoardingScreen()
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable(
            route = "habit_info/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: return@composable
            HabitInfoScreen(habitId = habitId, navController = navController)
        }
        composable("create_habit") {
            CreateHabitScreen(navController = navController)
        }
        composable(
            route = "calendar/{habitId}",
            arguments = listOf(navArgument("habitId") {type = NavType.IntType})
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: return@composable
            CalendarScreen(habitId = habitId)
        }
    }
}


