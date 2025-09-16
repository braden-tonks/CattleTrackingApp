package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Farmer
import com.example.cattletrackingapp.data.remote.FarmersApi

class FarmersRepository(
    private val api: FarmersApi = FarmersApi()
) {
    suspend fun fetchFarmers(): List<Farmer> {
        return api.getFarmers()
    }
}