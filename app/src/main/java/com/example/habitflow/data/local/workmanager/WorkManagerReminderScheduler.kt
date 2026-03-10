package com.example.habitflow.data.local.workmanager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.workDataOf
import androidx.work.WorkManager
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.scheduler.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {
    override fun schedule(habit: Habit) {
        val now = LocalDateTime.now()
        val todayReminder = LocalDateTime.of(LocalDate.now(), habit.reminder ?: return)

        val nextReminder = if (todayReminder.isBefore(now)) todayReminder.plusDays(1)
        else todayReminder

        val delayMinutes = ChronoUnit.MINUTES.between(
            now,
            nextReminder
        )

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf("habit_id" to habit.id))
            .addTag("reminder_${habit.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "reminder_${habit.id}",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )


    }

    override fun cancel(habitId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("reminder_${habitId}")
    }
}