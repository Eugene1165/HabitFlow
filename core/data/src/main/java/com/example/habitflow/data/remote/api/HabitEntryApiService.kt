package com.example.habitflow.data.remote.api

import com.example.habitflow.data.remote.dto.HabitEntryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface HabitEntryApiService {

    @GET("rest/v1/habit_entries")
    suspend fun getEntriesByHabitId(
        @Query("habit_id") habitId: String,
    ): List<HabitEntryDto>

    @POST("rest/v1/habit_entries")
    suspend fun createEntry(
        @Body habitEntryDto: HabitEntryDto
    ): Response<Unit>

    @PATCH("rest/v1/habit_entries")
    suspend fun updateEntryById(
        @Query("id") id: String,
        @Body habitEntry: HabitEntryDto
    ): Response<Unit>

}
