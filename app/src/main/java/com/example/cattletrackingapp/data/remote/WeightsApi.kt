package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Weight
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class WeightsApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getWeights(): List<Weight> {
        return client.from("weights")
            .select()
            .decodeList()
    }
}