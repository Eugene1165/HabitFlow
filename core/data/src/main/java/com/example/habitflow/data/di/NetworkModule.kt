package com.example.habitflow.data.di

import com.example.habitflow.data.remote.api.HabitApiService
import com.example.habitflow.data.remote.api.HabitEntryApiService
import com.example.habitflow.data.di.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(networkConfig: NetworkConfig): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (networkConfig.isDebug) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", networkConfig.apiKey)
                    .addHeader("Authorization", "Bearer ${networkConfig.apiKey}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    @Singleton
    fun provideHabitApiService(
        client: OkHttpClient,
        networkConfig: NetworkConfig
    ): HabitApiService {
        return Retrofit.Builder()
            .baseUrl(networkConfig.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHabitEntryApiService(
        client: OkHttpClient,
        networkConfig: NetworkConfig
    ): HabitEntryApiService {
        return Retrofit.Builder()
            .baseUrl(networkConfig.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitEntryApiService::class.java)
    }
}
