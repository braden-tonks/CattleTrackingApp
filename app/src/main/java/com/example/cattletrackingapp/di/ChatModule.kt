package com.example.cattletrackingapp.di

import com.example.cattletrackingapp.BuildConfig
import com.example.cattletrackingapp.ai.ChatProvider
import com.example.cattletrackingapp.ai.GeminiChatProvider
import com.example.cattletrackingapp.ai.LocalEchoChatProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides @Singleton
    fun provideChatProvider(): ChatProvider {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isNullOrBlank()) {
            LocalEchoChatProvider()
        } else {
            GeminiChatProvider(apiKey = key, modelName = "gemini-2.5-flash")
        }
    }
}
