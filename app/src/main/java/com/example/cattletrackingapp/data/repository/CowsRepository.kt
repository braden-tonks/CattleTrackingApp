package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.model.Farmer
import com.example.cattletrackingapp.data.remote.CowsApi
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.remote.FarmersApi

class CowsRepository (
    private val api: CowsApi = CowsApi()
){
    suspend fun addCow(cow: Cow): Boolean {
        return api.insertCow(cow)
    }

    suspend fun fetchCattleList(): List<Cow> {
        return api.getCows()
    }

}