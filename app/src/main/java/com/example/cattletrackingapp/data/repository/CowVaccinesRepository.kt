package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.CowVaccine
import com.example.cattletrackingapp.data.remote.CowVaccinesApi
import jakarta.inject.Singleton
import javax.inject.Inject

@Singleton
class CowVaccinesRepository @Inject constructor(
    private val api: CowVaccinesApi
) {

    suspend fun getCowVaccineByAnimalId(Id : String): List<CowVaccine> {
        return api.getCowVaccineByAnimalId(Id)
    }
}