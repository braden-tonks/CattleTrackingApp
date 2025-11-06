package com.example.cattletrackingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cattletrackingapp.data.local.entity.CowEntity
import com.example.cattletrackingapp.data.local.entity.FarmerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {

    @Query("SELECT * FROM farmers")
    fun getAllFarmers(): Flow<List<FarmerEntity>>

    @Query("SELECT * FROM farmers WHERE id = :id")
    suspend fun getFarmerById(id: String): FarmerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerEntity)

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    @Query("SELECT * FROM farmers WHERE pendingSync = 1")
    suspend fun getPendingSyncFarmers(): List<FarmerEntity>


}
