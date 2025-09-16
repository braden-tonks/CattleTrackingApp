package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Calf
import io.github.jan.supabase.postgrest.from

class CalvesApi {
    private val client = SupabaseClientProvider.client

    suspend fun getCalves(): List<Calf> {
        return client.from("calves")
            .select()
            .decodeList()
    }
}