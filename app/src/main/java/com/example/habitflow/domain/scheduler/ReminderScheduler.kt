package com.example.habitflow.domain.scheduler

import com.example.habitflow.domain.model.Habit

interface ReminderScheduler {
    fun schedule(habit: Habit)

    fun cancel(habitId: Int)

}