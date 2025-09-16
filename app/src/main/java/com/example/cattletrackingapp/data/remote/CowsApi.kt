package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Cow
import io.github.jan.supabase.postgrest.from

class CowsApi {
    private val client = SupabaseClientProvider.client

    suspend fun getFarmers(): List<Cow> {
        return client.from("cows")
            .select()
            .decodeList()
    }
}