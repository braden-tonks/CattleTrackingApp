package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Calf
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class CalvesApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getCalves(): List<Calf> {
        return client.from("calves")
            .select()
            .decodeList()
    }
}