package com.example.habitflow.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HabitDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("color") val color: String,
    @SerializedName("target") val target: Int?,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("repeat_type") val repeatType: String,
    @SerializedName("repeat_days") val repeatDays: String?,
    @SerializedName("repeat_count") val repeatCount: Int?,
    @SerializedName("reminder") val reminder: String?,
)
