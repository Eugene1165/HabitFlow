package com.example.habitflow.presentation.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val BOTTOM_NAV = "bottom_nav"
    const val HABIT_INFO = "habit_info/{habitId}"
    const val CREATE_HABIT = "create_habit?habitId={habitId}"
    const val CALENDAR = "calendar/{habitId}"

    fun habitInfo(habitId: Int) = "habit_info/$habitId"
    fun createHabit(habitId: Int) = "create_habit?habitId=$habitId"
    fun calendar(habitId: Int) = "calendar/$habitId"
}
