package com.example.cattletrackingapp.data.remote.Api

import com.example.cattletrackingapp.data.remote.Models.Farmer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class FarmersApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getFarmers(): List<Farmer> {
        return client.from("farmers")
            .select()
            .decodeList()
    }
}