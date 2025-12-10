package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.local.dao.BullDao
import com.example.cattletrackingapp.data.local.entity.BullEntity
import com.example.cattletrackingapp.data.mapper.toDto
import com.example.cattletrackingapp.data.mapper.toEntity
import com.example.cattletrackingapp.data.remote.Api.BullsApi
import com.example.cattletrackingapp.data.remote.Models.Bull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BullsRepository @Inject constructor(
    private val bullDao: BullDao,
    private val api: BullsApi
) {

    suspend fun searchBullByTag(tagNumber: String): List<Bull> {
        return api.searchBullByTag(tagNumber)

    }

    suspend fun getBullCount(): Int? {
        return api.getBullCount()
    }



    //The following is for offline mode

    val allBulls: Flow<List<Bull>> = bullDao.getAllBulls().map { list ->
        list.map { it.toDto() }
    }

    // Get all active bulls (Entity -> Model)
    val allActiveBulls: Flow<List<Bull>> = bullDao.getAllActiveBulls().map { list ->
        list.map { it.toDto() }
    }

    // View a cow offline
    suspend fun getBullById(id: String): Bull? {
        return bullDao.getBullById(id)?.toDto()
    }

    suspend fun addBull(bull: Bull): Boolean {
        val bullEntity: BullEntity = bull.toEntity()
        return try {
            bullDao.insertBull(
                bullEntity.copy(
                    pendingSync = true,
                    lastModified = System.currentTimeMillis()
                )
            )
            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateBull(bull: Bull): Boolean {
        val bullEntity: BullEntity = bull.toEntity()
        return try {
            bullDao.updateBull(
                bullEntity.copy(
                    pendingSync = true,
                    lastModified = System.currentTimeMillis()
                )
            )
            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun syncPendingBulls() {
        val pending = bullDao.getPendingSyncBulls()
        println("SyncDebug: Found ${pending.size} cows pending sync")
        pending.forEach { entity ->
            try {
                val dto = entity.toDto()
                println("SyncDebug: Uploading cow ${entity.id} (${entity.tag_number})")
                val syncSuccess = api.upsertBull(dto)
                if(syncSuccess) {
                    bullDao.updateBull(entity.copy(pendingSync = false))
                    println("SyncDebug: Pending Sync = ${entity.pendingSync} for (${entity.tag_number})")
                } else {
                    println("SyncDebug: Offline and Pending Sync is still (${entity.pendingSync})")
                }
            } catch (e: Exception) {
                println("Sync error: ${e.message}")
            }
        }
    }

    suspend fun refreshBullsFromApi() {
        try {
            // Try to fetch from Supabase
            val remoteBulls = api.getBulls()

            // Only proceed if we actually got results
            if (remoteBulls.isNotEmpty()) {
                // Clear local data and insert the new version
                bullDao.deleteAllBulls()
                remoteBulls.forEach { bull ->
                    bullDao.insertBull(bull.toEntity())
                }
            }
        } catch (e: Exception) { /* offline: ignore */ }
    }


    suspend fun clearLocalBulls() {
        bullDao.deleteAllBulls()
    }

}