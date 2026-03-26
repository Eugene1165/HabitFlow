package com.example.habitflow.data.local.workmanager

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitflow.domain.model.Habit
import com.example.habitflow.domain.repository.HabitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker

class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: HabitRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val habitId = inputData.getInt("habit_id", DEFAULT_VALUE)
        if (habitId == -1) return Result.failure()
        val habit = repository.getHabitById(habitId)
        habit?.let { h ->
            if (!h.isArchived) showNotification(h)
        }
        return Result.success()
    }


    private fun showNotification(habit: Habit) {
        //обязательный канал для уведомления
        val channel = NotificationChannel(
            "habits_channel",
            "Напоминания о привычках",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        //само уведомление,строим его сами
        val notification = NotificationCompat.Builder(applicationContext, "habits_channel")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("HabitFlow")
            .setContentText("Не забудь: ${habit.title}")
            .build()

        //показываем уведомление
        val notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(habit.id, notification)
    }

    companion object {
        const val DEFAULT_VALUE = -1
    }

}
