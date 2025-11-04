package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.model.Calf
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count


class CalvesApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getCalves(): List<Calf> {
        return client.from("calves")
            .select()
            .decodeList()
    }

    suspend fun insertCalf(calf: Calf): Boolean {
        return try {
            client.from("calves").insert(calf)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCalfById(id: String): Calf? {
        return client.from("calves")
            .select()
            .decodeList<Calf>()
            .firstOrNull { it.id == id }
    }

    suspend fun getCalvesByParentId(id: String): List<Calf> {
        return client.from("calves")
            .select() {
                filter {
                    or {
                        eq("cow_id", id)
                        eq("bull_id", id)
                    }
                }
            }
            .decodeList<Calf>()

    }

    suspend fun listCalfWeight(): List<Calf> {
        val calves = client.from("calf")
            .select()
            .decodeList<Calf>()

        return calves.sortedByDescending { it.current_weight ?: 0.0 }
    }

    //This is for the search bar component
    suspend fun searchCalfByTag(tagNumber: String): List<Calf> {
        return client.from("calves")
            .select() {
                filter {
                    ilike("tag_number", "%$tagNumber%")
                }
            }
            .decodeList<Calf>()

    }

    suspend fun getCalfCount(): Int? {
        return client.from("calves")
            .select {
                count(Count.EXACT)
            }
            .countOrNull()?.toInt() ?: 0
    }
}