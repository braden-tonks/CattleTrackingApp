package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Vaccine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class VaccinesApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getVaccines(): List<Vaccine> {
        return client.from("vaccines")
            .select()
            .decodeList()
    }
}