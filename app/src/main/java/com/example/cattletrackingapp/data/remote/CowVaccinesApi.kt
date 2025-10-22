package com.example.cattletrackingapp.data.remote

import com.example.cattletrackingapp.data.model.CowVaccine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import jakarta.inject.Inject

class CowVaccinesApi @Inject constructor (private val client: SupabaseClient){

    suspend fun getCowVaccines(): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select()
            .decodeList()
    }

    suspend fun getCowVaccineByAnimalId(id: String): List<CowVaccine> {
        return client.from("cow_vaccines")
            .select(Columns.list("id", "date_given", "remarks")) {
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