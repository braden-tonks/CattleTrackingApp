package com.example.cattletrackingapp.data.remote.Api

import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.Weight
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import jakarta.inject.Inject


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

    suspend fun upsertCalf(calf: Calf): Boolean {
        // onConflict = "id" or "tag_number" depending on your unique column
        return try {
            client.from("calves")
                .upsert(calf)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            println("SyncDebug: Full Calf DTO:$calf")
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

    suspend fun reloadCurrentWeight(weight: Weight, id: String, days: Int): Boolean {
        return try {
            client.from("calves").update(
                {
                    set("current_weight", weight.weight)
                }
            ) {
                filter {
                    eq("id", id)
                }
            }

            val avg_gain = ((weight.weight - 40.0)/days)

            client.from("calves").update(
                {
                    set("avg_gain", avg_gain)
                }
            ) {
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}