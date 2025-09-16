package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Weight
import io.github.jan.supabase.postgrest.from

class WeightsApi {
    private val client = SupabaseClientProvider.client

    suspend fun getWeights(): List<Weight> {
        return client.from("weights")
            .select()
            .decodeList()
    }
}