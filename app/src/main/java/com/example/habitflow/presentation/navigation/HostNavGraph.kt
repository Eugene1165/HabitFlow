package com.example.habitflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitflow.feature.habits.calendar.CalendarScreen
import com.example.habitflow.feature.habits.create.CreateHabitScreen
import com.example.habitflow.feature.habits.info.HabitInfoScreen
import com.example.habitflow.feature.onboarding.OnBoardingScreen
import com.example.habitflow.presentation.main.BottomNav

@Composable
fun HostNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING
    ) {
        composable(Routes.ONBOARDING) {
            OnBoardingScreen(navController)
        }
        composable(Routes.BOTTOM_NAV) {
            BottomNav(navController = navController)
        }
        composable(
            route = Routes.HABIT_INFO,
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: return@composable
            HabitInfoScreen(habitId = habitId, navController = navController)
        }
        composable(
            route = Routes.CREATE_HABIT,
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { _ -> CreateHabitScreen(navController = navController) }
        composable(
            route = Routes.CALENDAR,
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { _ -> CalendarScreen(navController) }
    }
}


