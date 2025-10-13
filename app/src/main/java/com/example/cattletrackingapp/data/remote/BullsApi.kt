package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.model.Cow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class BullsApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getBulls(): List<Bull> {
        return client.from("bulls")
            .select()
            .decodeList()
    }

    suspend fun insertBull(bull: Bull): Boolean {
        return try {
            client.from("bulls").insert(bull)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}