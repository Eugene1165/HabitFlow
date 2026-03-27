package com.example.habitflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitflow.feature.habits.calendar.CalendarScreen
import com.example.habitflow.feature.habits.info.HabitInfoScreen
import com.example.habitflow.feature.onboarding.OnBoardingScreen
import com.example.habitflow.feature.habits.create.CreateHabitScreen

import com.example.habitflow.presentation.main.MainScreen

@Composable
fun HostNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnBoardingScreen(navController)
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
        composable(
            route = "create_habit?habitId={habitId}",
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId")?.takeIf { it != -1 }
            CreateHabitScreen(habitId=habitId,navController=navController,)
        }
        composable(
            route = "calendar/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { _ -> CalendarScreen(navController)
        }
    }
}


