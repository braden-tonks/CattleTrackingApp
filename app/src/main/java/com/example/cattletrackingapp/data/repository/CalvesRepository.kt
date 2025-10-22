package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.remote.CalvesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalvesRepository @Inject constructor(
    private val api: CalvesApi
) {

    suspend fun fetchCalves(): List<Calf> {
        return api.getCalves()
    }

    suspend fun addCalf(calf: Calf): Boolean {
        return api.insertCalf(calf)
    }

    suspend fun getCalfById(id: String): Calf? {
        return api.getCalfById(id)
    }

    suspend fun getCalvesByDamId(id: String): List<Calf> {
        return api.getCalvesByParentId(id)
    }
}