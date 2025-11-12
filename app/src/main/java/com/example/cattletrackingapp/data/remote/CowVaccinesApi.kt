package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.CowVaccine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class CowVaccinesApi @Inject constructor (private val client: SupabaseClient){

    /** Bulk insert rows (animal × vaccine). Returns true on success. */
    suspend fun insertMany(rows: List<Map<String, String?>>): Boolean {
        return try {
            client.from("cow_vaccines").insert(rows)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCowVaccines(): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select()
            .decodeList()
    }

    suspend fun getCowVaccineByAnimalId(id: String): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select() {
                filter {
                    or {
                        eq("cow_id", id)
                        eq("calf_id", id)
                        eq("bull_id", id)
                    }
                }
            }
            .decodeList<CowVaccine>()

    }
}