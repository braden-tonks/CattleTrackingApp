package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Farmer
import io.github.jan.supabase.postgrest.from

class FarmersApi {
    private val client = SupabaseClientProvider.client

    suspend fun getFarmers(): List<Farmer> {
        return client.from("farmer")
            .select()
            .decodeList()
    }
}