package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Bull
import io.github.jan.supabase.postgrest.from

class BullsApi {
    private val client = SupabaseClientProvider.client

    suspend fun getBulls(): List<Bull> {
        return client.from("bulls")
            .select()
            .decodeList()
    }
}