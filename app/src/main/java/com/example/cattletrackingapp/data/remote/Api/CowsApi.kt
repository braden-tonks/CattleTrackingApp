package com.example.cattletrackingapp.data.remote.Api

//this page is the data access layer that fetches raw 'cow' data from Supabase

import com.example.cattletrackingapp.data.remote.Models.Cow
import com.example.cattletrackingapp.data.remote.Models.CowIdAndTag
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
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

    //This is for the search bar component
    suspend fun searchCowByTag(tagNumber: String): List<Cow> {
        return client.from("cows")
            .select() {
                filter {
                    ilike("tag_number", "%$tagNumber%")
                }
            }
            .decodeList<Cow>()
    }

    suspend fun getCowCount(): Int? {
        return client.from("cows")
            .select {
                count(Count.EXACT)
            }
            .countOrNull()?.toInt() ?: 0
    }
}