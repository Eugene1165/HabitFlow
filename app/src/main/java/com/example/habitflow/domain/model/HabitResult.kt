package com.example.habitflow.domain.model

sealed class HabitResult<out T> {
    data class Success<out T>(val data: T) : HabitResult<T>()
    data class Error(val exception: Throwable, val message: String) : HabitResult<Nothing>()
}





