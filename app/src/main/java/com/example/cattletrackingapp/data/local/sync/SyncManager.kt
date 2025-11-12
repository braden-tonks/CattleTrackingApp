package com.example.cattletrackingapp.data.local.sync

import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class SyncManager @Inject constructor(
    private val cowsRepository: CowsRepository,
    private val calvesRepository: CalvesRepository,
    private val bullsRepository: BullsRepository
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    /** Call this to sync everything in the background */
    fun syncAll() {
        scope.launch {
            try {
                // Push local changes first
                cowsRepository.syncPendingCows()
                calvesRepository.syncPendingCalves()
                bullsRepository.syncPendingBulls()

                // Optionally pull fresh data from Supabase
                cowsRepository.refreshCowsFromApi()
                calvesRepository.refreshCalvesFromApi()
                bullsRepository.refreshBullsFromApi()

            } catch (e: Exception) {
                println("Sync error: ${e.message}")
            }
        }
    }
}