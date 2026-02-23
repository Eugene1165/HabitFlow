package com.example.habitflow.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habitflow.presentation.habits.list.HabitsListScreen
import com.example.habitflow.presentation.navigation.NavigationItem
import com.example.habitflow.presentation.settings.SettingsScreen
import com.example.habitflow.presentation.statistics.StatisticsScreen

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    NavigationItem.Habits,
                    NavigationItem.Statistics,
                    NavigationItem.Settings
                )
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(text = item.title) },
                    )

                }


            }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = bottomNavController,
            startDestination = NavigationItem.Habits.route
        ) {
            composable(NavigationItem.Habits.route) { HabitsListScreen(navController) }
            composable(NavigationItem.Statistics.route) { StatisticsScreen() }
            composable(NavigationItem.Settings.route) { SettingsScreen() }


        }
    }

}