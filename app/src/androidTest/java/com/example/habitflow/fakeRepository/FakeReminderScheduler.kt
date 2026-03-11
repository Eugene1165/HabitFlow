package com.example.habitflow.fakeRepository

import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.scheduler.ReminderScheduler

class FakeReminderScheduler: ReminderScheduler {
    override fun schedule(habit: Habit){}

    override fun cancel(habitId: Int) {}
}