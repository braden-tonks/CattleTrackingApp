package com.example.cattletrackingapp.data.repository

// This is the repository layer, the go-between for our API and the ViewModel

import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.remote.CowsApi

class CowsRepository (
    private val api: CowsApi = CowsApi()
){
    suspend fun fetchCattleList(): List<Cow> {
        return api.getCows()
    }

}
