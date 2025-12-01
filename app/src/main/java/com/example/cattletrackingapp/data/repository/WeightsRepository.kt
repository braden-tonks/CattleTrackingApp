package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.remote.Api.WeightsApi
import com.example.cattletrackingapp.data.remote.Models.Weight
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class WeightsRepository @Inject constructor(
    private val api: WeightsApi,
) {

    suspend fun getWeightById(id: String): List<Weight> {
        println("SyncDebug: Getting weights for $id")
        return api.getWeightsById(id)
    }
}