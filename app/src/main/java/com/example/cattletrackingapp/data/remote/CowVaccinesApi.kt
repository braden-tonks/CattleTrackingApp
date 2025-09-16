package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.CowVaccine
import io.github.jan.supabase.postgrest.from

class CowVaccinesApi {
    private val client = SupabaseClientProvider.client

    suspend fun getFarmers(): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select()
            .decodeList()
    }
}