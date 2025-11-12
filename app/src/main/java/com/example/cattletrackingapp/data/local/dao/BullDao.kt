package com.example.cattletrackingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cattletrackingapp.data.local.entity.BullEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BullDao {

    @Query("SELECT * FROM bulls")
    fun getAllBulls(): Flow<List<BullEntity>>

    @Query("SELECT * FROM bulls WHERE id = :id")
    suspend fun getBullById(id: String): BullEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBull(bull: BullEntity)

    @Update
    suspend fun updateBull(bull: BullEntity)

    @Query("SELECT * FROM bulls WHERE pendingSync = 1")
    suspend fun getPendingSyncBulls(): List<BullEntity>

    @Query("DELETE FROM bulls")
    suspend fun deleteAllBulls()
}
