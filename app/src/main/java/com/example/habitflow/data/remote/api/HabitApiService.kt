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

    @GET("rest/v1/habits")
    suspend fun getAllEntries(): List<HabitDto>

    @POST("rest/v1/habits")
    suspend fun createEntries(
        @Body habit: HabitDto
    ): HabitDto


    @PATCH("rest/v1/habits")
    suspend fun updateEntriesById(
        @Query("id") habitId: String,
        @Body habit: HabitDto
    ): HabitDto

    @DELETE("rest/v1/habits")
    suspend fun removeEntriesById(
        @Query("id") habitId: String,
    ): Response<Unit>

}