package com.example.cattletrackingapp.data.repository

// This is the repository layer, the go-between for our API and the ViewModel

import com.example.cattletrackingapp.data.remote.CowsApi
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.model.CowIdAndTag
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CowsRepository @Inject constructor(
    private val api: CowsApi
) {

    suspend fun addCow(cow: Cow): Boolean {
        return api.insertCow(cow)
    }

    suspend fun fetchCattleList(): List<Cow> {
        return api.getCows()
    }

    suspend fun getCowById(id: String): Cow? {
        return api.getCowById(id)

    }


    suspend fun fetchCattleIdsAndTags(): List<CowIdAndTag> {
        return api.getCowIdsAndTags()
    }

    suspend fun searchCowByTag(tagNumber: String): List<Cow> {
        return api.searchCowByTag(tagNumber)
    }
}

