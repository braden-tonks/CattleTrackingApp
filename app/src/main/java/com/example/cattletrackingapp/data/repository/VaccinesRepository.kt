package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.remote.Models.Vaccine
import com.example.cattletrackingapp.data.remote.Api.VaccinesApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class VaccinesRepository @Inject constructor(
    private val api: VaccinesApi
) {
    suspend fun fetchVaccines(): List<Vaccine> {
        return api.getVaccines()
    }

    suspend fun addVaccine(vaccine: Vaccine): Boolean {
        return api.insertVaccine(vaccine)
    }

    suspend fun getVaccineById(id: String): Vaccine? {
        return api.getVaccineById(id)
    }

    suspend fun deleteVaccine(vaccineId: String): Boolean = api.deleteVaccine(vaccineId)

}
