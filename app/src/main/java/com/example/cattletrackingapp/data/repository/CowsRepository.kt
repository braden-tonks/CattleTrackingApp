package com.example.cattletrackingapp.data.repository

// This is the repository layer, the go-between for our API and the ViewModel

import com.example.cattletrackingapp.data.local.dao.CowDao
import com.example.cattletrackingapp.data.local.entity.CowEntity
import com.example.cattletrackingapp.data.mapper.toDto
import com.example.cattletrackingapp.data.mapper.toEntity
import com.example.cattletrackingapp.data.remote.Api.CowsApi
import com.example.cattletrackingapp.data.remote.Models.Cow
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CowsRepository @Inject constructor(
    private val api: CowsApi,
    private val cowDao: CowDao
) {

    suspend fun searchCowByTag(tagNumber: String): List<Cow> {
        return api.searchCowByTag(tagNumber)
    }

    suspend fun getCowCount(): Int? {
        return api.getCowCount()
    }


    //These next functions are used for the Offline mode of the app
    // Expose offline data for UI
    // Observe all cows (Entity -> Model)
    val allCows: Flow<List<Cow>> = cowDao.getAllCows().map { list ->
        list.map { it.toDto() }
    }


    //Gets all active Cows (Entity -> Model)
    val allActiveCows: Flow<List<Cow>> = cowDao.getAllActiveCows().map { list ->
        list.map { it.toDto() }
    }

    // Insert a cow offline
    suspend fun addCow(cow: Cow): Boolean {
        val cowEntity: CowEntity = cow.toEntity()
        return try {
            cowDao.insertCow(
                cowEntity.copy(
                    pendingSync = true,
                    lastModified = System.currentTimeMillis()
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // View a cow offline
    suspend fun getCowById(id: String): Cow? {
        return cowDao.getCowById(id)?.toDto()
    }


    suspend fun updateCow(cow: Cow): Boolean {
        val cowEntity: CowEntity = cow.toEntity()
        return try {
            cowDao.updateCow(
                cowEntity.copy(
                    pendingSync = true,
                    lastModified = System.currentTimeMillis()
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Push pending cows to Supabase
    suspend fun syncPendingCows() {
        val pending = cowDao.getPendingSyncCows()
        pending.forEach { entity ->
            try {
                val dto = entity.toDto()
                println("SyncDebug: Uploading cow ${entity.id} (${entity.tag_number})")
                val syncSuccess = api.upsertCow(dto)
                if (syncSuccess) {
                    cowDao.updateCow(entity.copy(pendingSync = false))
                    println("SyncDebug: Pending Sync = ${entity.pendingSync} for (${entity.tag_number})")
                } else {
                    println("SyncDebug: Offline and Pending Sync is still (${entity.pendingSync})")
                }
            } catch (e: Exception) {
                println("Sync error: ${e.message}")
            }
        }
    }

    // Fetch fresh cows from Supabase and update local DB
    suspend fun refreshCowsFromApi() {
        try {
            // Try to fetch from Supabase
            val remoteCows = api.getCows()

            // Only proceed if we actually got results
            if (remoteCows.isNotEmpty()) {
                // Clear local data and insert the new version
                cowDao.deleteAllCows()
                remoteCows.forEach { cow ->
                    cowDao.insertCow(cow.toEntity())
                }
            }
        } catch (e: Exception) {
            // offline: ignore
        }
    }
}

