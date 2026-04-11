package com.example.habitflow.data.remote.api

import retrofit2.http.Query
import com.example.habitflow.data.remote.dto.HabitDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface HabitApiService {

    @GET("rest/v1/Habit")
    suspend fun getAllEntries(
        @Query("is_archived") isArchived: String
    ): List<HabitDto>

    @POST("rest/v1/Habit")
    suspend fun createEntries(
        @Body habit: HabitDto
    ): Response<Unit>


    @PATCH("rest/v1/Habit")
    suspend fun updateEntriesById(
        @Query("id") habitId: String,
        @Body habit: HabitDto
    ): Response<Unit>

    @DELETE("rest/v1/Habit")
    suspend fun removeEntriesById(
        @Query("id") habitId: String,
    ): Response<Unit>

}
