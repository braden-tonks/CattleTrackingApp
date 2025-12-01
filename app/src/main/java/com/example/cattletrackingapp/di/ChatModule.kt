package com.example.cattletrackingapp.di

import com.example.cattletrackingapp.BuildConfig
import com.example.cattletrackingapp.ai.ChatProvider
import com.example.cattletrackingapp.ai.ChatRetrieval
import com.example.cattletrackingapp.ai.ChatService
import com.example.cattletrackingapp.ai.GeminiChatProvider
import com.example.cattletrackingapp.ai.LocalEchoChatProvider
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
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
        val key: String = BuildConfig.GEMINI_API_KEY
        return if (key.isBlank()) LocalEchoChatProvider()
        else GeminiChatProvider(apiKey = key, modelName = "gemini-2.5-flash")
    }

    // ChatRetrieval(cows, bulls, calves)
    @Provides @Singleton
    fun provideChatRetrieval(
        cows: CowsRepository,
        bulls: BullsRepository,
        calves: CalvesRepository
    ): ChatRetrieval = ChatRetrieval(cows, bulls, calves)

    @Provides @Singleton
    fun provideChatService(
        retrieval: ChatRetrieval,
        provider: ChatProvider
    ): ChatService = ChatService(retrieval, provider)
}
