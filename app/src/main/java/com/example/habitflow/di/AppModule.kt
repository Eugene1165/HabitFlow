package com.example.habitflow.di

import com.example.habitflow.data.di.NetworkConfig
import com.example.habitflow.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = NetworkConfig(
        baseUrl = BuildConfig.SUPABASE_URL,
        apiKey = BuildConfig.SUPABASE_KEY
    )
}
