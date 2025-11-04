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
        return api.getBullById(id)
    }

    suspend fun addBull(bull: Bull): Boolean {
        return api.insertBull(bull)
    }

    suspend fun fetchBullsList(): List<Bull> {
        return api.getBulls()
    }

    suspend fun searchBullByTag(tagNumber: String): List<Bull> {
        return api.searchBullByTag(tagNumber)

    }

    suspend fun getBullCount(): Int? {
        return api.getBullCount()
    }



}