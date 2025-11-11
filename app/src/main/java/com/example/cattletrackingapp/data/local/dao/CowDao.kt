package com.example.cattletrackingapp.data.local.dao

import androidx.room.*
import com.example.cattletrackingapp.data.local.entity.CowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Query("SELECT * FROM cows")
    fun getAllCows(): Flow<List<CowEntity>>

    @Query("SELECT * FROM cows WHERE id = :id")
    suspend fun getCowById(id: String): CowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cow: CowEntity)

    @Update
    suspend fun updateCow(cow: CowEntity)

    // For offline sync
    @Query("SELECT * FROM cows WHERE pendingSync = 1")
    suspend fun getPendingSyncCows(): List<CowEntity>

    @Query("DELETE FROM cows")
    suspend fun deleteAllCows()
}
