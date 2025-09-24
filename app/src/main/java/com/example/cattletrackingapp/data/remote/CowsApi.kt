package com.example.cattletrackingapp.data.remote

//this page is the data access layer that fetches raw 'cow' data from Supabase

import com.example.cattletrackingapp.data.model.Cow
import io.github.jan.supabase.postgrest.from

class CowsApi {
    private val client = SupabaseClientProvider.client

    suspend fun getCows(): List<Cow> {
        return client.from("cows")
            .select()
            .decodeList()
    }

    suspend fun insertCow(cow: Cow): Boolean {
        return try {
            client.from("cows").insert(cow)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}