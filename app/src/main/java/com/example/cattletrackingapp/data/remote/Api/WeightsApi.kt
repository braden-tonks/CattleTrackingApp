package com.example.cattletrackingapp.data.remote.Api

import com.example.cattletrackingapp.data.remote.Models.Weight
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class WeightsApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getWeights(): List<Weight> {
        return client.from("weights")
            .select()
            .decodeList()
    }

    suspend fun insertWeight(weight: Weight): Boolean {
        return try {
            client.from("weights").insert(weight)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}