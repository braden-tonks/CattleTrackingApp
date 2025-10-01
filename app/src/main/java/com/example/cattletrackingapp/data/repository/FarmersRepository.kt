package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Farmer
import com.example.cattletrackingapp.data.remote.FarmersApi
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FarmersRepository @Inject constructor(
    private val api: FarmersApi
) {
    suspend fun fetchFarmers(): List<Farmer> {
        return api.getFarmers()
    }
}