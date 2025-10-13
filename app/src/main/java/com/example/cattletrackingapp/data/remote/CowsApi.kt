package com.example.cattletrackingapp.data.remote

//this page is the data access layer that fetches raw 'cow' data from Supabase

import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.model.CowIdAndTag
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import jakarta.inject.Inject


class CowsApi @Inject constructor (private val client: SupabaseClient) {

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

    suspend fun getCowById(id: String): Cow? {
        return client.from("cows")
            .select()
            .decodeList<Cow>()
            .firstOrNull { it.id == id }
    }

    suspend fun getCowIdsAndTags(): List<CowIdAndTag> {
        return client.from("cows")
            .select(Columns.list("id", "tag_number"))
            .decodeList<CowIdAndTag>()
    }
}