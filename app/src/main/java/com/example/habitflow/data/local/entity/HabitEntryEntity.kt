package com.example.habitflow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_entries", foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = CASCADE
    )], indices = [Index(value = ["habitId"])]
)
data class HabitEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val habitId: Int,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val isDone: Boolean
)