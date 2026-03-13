package com.example.habitflow.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HabitEntryDto (
    @SerializedName("id") val id: Int,
    @SerializedName("habit_id")val habitId: Int,
    @SerializedName("date")val date: String,
    @SerializedName("is_done") val isDone: Boolean
)
