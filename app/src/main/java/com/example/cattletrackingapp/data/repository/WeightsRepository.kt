package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.remote.Api.WeightsApi
import com.example.cattletrackingapp.data.remote.Models.Weight
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightsRepository @Inject constructor(
    private val api: WeightsApi
) {
    suspend fun fetchWeights(): List<Weight> {
        return api.getWeights()
    }

    suspend fun addWeight(weight: Weight): Boolean {
        return api.insertWeight(weight)
        
    
    suspend fun getWeightById(id: String): List<Weight> {
        println("SyncDebug: Getting weights for $id")
        return api.getWeightsById(id)
    }
}