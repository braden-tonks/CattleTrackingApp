package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.local.dao.CalfDao
import com.example.cattletrackingapp.data.local.entity.CalfEntity
import com.example.cattletrackingapp.data.mapper.toDto
import com.example.cattletrackingapp.data.mapper.toEntity
import com.example.cattletrackingapp.data.remote.Api.CalvesApi
import com.example.cattletrackingapp.data.remote.Models.Calf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalvesRepository @Inject constructor(
    private val calfDao: CalfDao,
    private val api: CalvesApi
) {

    suspend fun fetchCalves(): List<Calf> {
        return api.getCalves()
    }

    suspend fun listCalfWeight(): List<Calf> {
        return api.listCalfWeight()
    }

    suspend fun searchCalfByTag(tagNumber: String): List<Calf> {
        return api.searchCalfByTag(tagNumber)
    }

    suspend fun getCalfCount(): Int? {
        return api.getCalfCount()
    }


    //These next functions is for offline mode

    // Offline Flow for UI
    val allCalves: Flow<List<Calf>> = calfDao.getAllCalves().map { list ->
        list.map { it.toDto() }
    }

    // View a cow offline
    suspend fun getCalfById(id: String): Calf? {
        return calfDao.getCalfById(id)?.toDto()
    }

    // get all calves that were born from a particular cow or bull
    suspend fun allCalvesByParentTag(id: String?): List<Calf> {
        return calfDao.getCalvesByParentId(id).map { it.toDto() }
    }


    suspend fun addCalf(calf: Calf): Boolean {
        val calfEntity: CalfEntity = calf.toEntity()
        return try {
            calfDao.insertCalf(
                calfEntity.copy(
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

    suspend fun updateCalf(calfEntity: CalfEntity) {
        calfDao.updateCalf(
            calfEntity.copy(
                pendingSync = true,
                lastModified = System.currentTimeMillis()
            )
        )
    }

    // Offline sync
    suspend fun syncPendingCalves() {
        val pending = calfDao.getPendingSyncCalves()
        pending.forEach { entity ->
            try {
                val dto = entity.toDto()
                println("SyncDebug: Uploading cow ${entity.id} (${entity.tag_number})")
                val syncSuccess = api.insertCalf(dto)
                if (syncSuccess) {
                    calfDao.updateCalf(entity.copy(pendingSync = false))
                    println("SyncDebug: Pending Sync = ${entity.pendingSync} for (${entity.tag_number})")
                } else {
                    println("SyncDebug: Offline and Pending Sync is still (${entity.pendingSync})")
                }
            } catch (e: Exception) {
                println("Sync error: ${e.message}")
            }
        }
    }

    // Refresh local DB from API
    suspend fun refreshCalvesFromApi() {
        try {
            // Try to fetch from Supabase
            val remoteCalves = api.getCalves()

            // Only proceed if we actually got results
            if (remoteCalves.isNotEmpty()) {
                // Clear local data and insert the new version
                calfDao.deleteAllCalves()
                remoteCalves.forEach { calf ->
                    calfDao.insertCalf(calf.toEntity())
                }
            }
        } catch (e: Exception) { /* offline: ignore */
        }
    }
}