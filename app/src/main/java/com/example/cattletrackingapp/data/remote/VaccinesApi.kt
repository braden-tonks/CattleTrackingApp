package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Vaccine
import io.github.jan.supabase.postgrest.from

class VaccinesApi {
    private val client = SupabaseClientProvider.client

    suspend fun getVaccines(): List<Vaccine> {
        return client.from("vaccines")
            .select()
            .decodeList()
    }
}