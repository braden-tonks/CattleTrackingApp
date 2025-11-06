package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.remote.Models.Farmer
import com.example.cattletrackingapp.data.remote.Api.FarmersApi
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