package com.example.cattletrackingapp.data.remote.Api

// Data access for the `vaccines` table (list of all vaccines)

import com.example.cattletrackingapp.data.remote.Models.Vaccine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject

class VaccinesApi @Inject constructor(
    private val client: SupabaseClient
) {
    /** Get all vaccines (we only show name in UI for now). */
    suspend fun getVaccines(): List<Vaccine> {
        return client.from("vaccines")
            .select()              // pulls: id, farmer_id, vaccine_number, name, description, notes, created_at
            .decodeList()
    }

    /** Optional: add a new vaccine to the catalog. */
    suspend fun insertVaccine(vaccine: Vaccine): Boolean {
        return try {
            client.from("vaccines").insert(vaccine)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** Optional: get a single vaccine by id (version-proof – filters in memory). */
    suspend fun getVaccineById(id: String): Vaccine? {
        return client.from("vaccines")
            .select()
            .decodeList<Vaccine>()
            .firstOrNull { it.id == id }
    }

    suspend fun deleteVaccine(vaccineId: String): Boolean {
        return try {
            client.from("vaccines").delete { filter { eq("id", vaccineId) } }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
