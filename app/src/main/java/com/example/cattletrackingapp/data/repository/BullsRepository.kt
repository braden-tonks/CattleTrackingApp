package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.model.BullIdAndTag
import com.example.cattletrackingapp.data.remote.BullsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BullsRepository @Inject constructor(
    private val api: BullsApi
) {

    suspend fun fetchBullIdsAndTags(): List<BullIdAndTag> {
        return api.getBullIdsAndTags()
    }

    suspend fun fetchBullById(id: String): Bull? {
        return api.getCowById(id)
    }

    suspend fun addBull(bull: Bull): Boolean {
        return api.insertBull(bull)
    }

    suspend fun fetchBullsList(): List<Bull> {
        return api.getBulls()
    }


}