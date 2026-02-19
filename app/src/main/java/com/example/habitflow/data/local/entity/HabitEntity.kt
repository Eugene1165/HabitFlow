package com.example.habitflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val description: String?,
    @ColumnInfo
    val startDate: String,
    @ColumnInfo
    val color: String,
    @ColumnInfo
    val isArchived: Boolean,
    @ColumnInfo
    val repeatType: String,
    @ColumnInfo
    val repeatDays: String?,
    @ColumnInfo
    val repeatCount: Int?,
    @ColumnInfo
    val reminder: String?,
    @ColumnInfo
    val target: Int?
)