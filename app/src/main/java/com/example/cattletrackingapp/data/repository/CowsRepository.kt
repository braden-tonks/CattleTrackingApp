package com.example.cattletrackingapp.data.repository

// This is the repository layer, the go-between for our API and the ViewModel

import com.example.cattletrackingapp.data.model.Farmer
import com.example.cattletrackingapp.data.remote.CowsApi
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.remote.FarmersApi
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CowsRepository @Inject constructor(
    private val api: CowsApi
){

    suspend fun addCow(cow: Cow): Boolean {
        return api.insertCow(cow)
    }

    suspend fun fetchCattleList(): List<Cow> {
        return api.getCows()
    }
}
