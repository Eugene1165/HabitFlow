package com.example.habitflow.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Star


sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    object Habits: NavigationItem(
        route = "habits_list",
        title = "Привычки",
        icon = Icons.Default.Home
    )

    object Statistics: NavigationItem(
        route = "habits_statistics",
        title = "Статистика",
        icon = Icons.Filled.DateRange
    )
    object Settings: NavigationItem(
        route = "settings",
        title = "Настройки",
        icon = Icons.Default.Settings
    )
    object Archived: NavigationItem(
        route = "archived",
        title = "Архив",
        icon = Icons.Default.Star
    )
}
