package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.CowVaccine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class CowVaccinesApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getCowVaccines(): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select()
            .decodeList()
    }
}