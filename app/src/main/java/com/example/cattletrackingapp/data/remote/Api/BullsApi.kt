package com.example.cattletrackingapp.data.remote.Api

import com.example.cattletrackingapp.data.remote.Models.Bull
import com.example.cattletrackingapp.data.remote.Models.BullIdAndTag
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import jakarta.inject.Inject

class BullsApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getBulls(): List<Bull> {
        return client.from("bulls")
            .select()
            .decodeList()
    }

    suspend fun getBullById(id: String): Bull? {
        return client.from("bulls")
            .select()
            .decodeList<Bull>()
            .firstOrNull { it.id == id }
    }


    suspend fun getBullIdsAndTags(): List<BullIdAndTag> {
        return client.from("bulls")
            .select(Columns.list("id", "tag_number"))
            .decodeList<BullIdAndTag>()
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

    suspend fun upsertBull(bull: Bull): Boolean {
        return try {
            client.from("bulls").upsert(bull)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    //This is for the search bar component
    suspend fun searchBullByTag(tagNumber: String): List<Bull> {
        return client.from("bulls")
            .select() {
                filter {
                    ilike("tag_number", "%$tagNumber%")
                }
            }
            .decodeList<Bull>()

    }

    suspend fun getBullCount(): Int? {
        return client.from("bulls")
            .select {
                count(Count.EXACT)
            }
            .countOrNull()?.toInt() ?: 0
    }
}