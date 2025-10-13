package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.remote.BullsApi
import javax.inject.Inject

class BullsRepository @Inject constructor(
    private val api: BullsApi
){
    suspend fun addBull(bull: Bull): Boolean {
        return api.insertBull(bull)
    }

    suspend fun fetchCattleList(): List<Bull> {
        return api.getBulls()
    }
}