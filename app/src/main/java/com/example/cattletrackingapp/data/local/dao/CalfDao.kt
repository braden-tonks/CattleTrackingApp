package com.example.cattletrackingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cattletrackingapp.data.local.entity.CalfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalfDao {

    @Query("SELECT * FROM calves")
    fun getAllCalves(): Flow<List<CalfEntity>>

    @Query("SELECT * FROM calves WHERE is_active = 1")
    fun getAllActiveCalves(): Flow<List<CalfEntity>>

    @Query("SELECT * FROM calves WHERE id = :id")
    suspend fun getCalfById(id: String): CalfEntity?

    @Query("SELECT * FROM calves WHERE cow_id = :parentId OR bull_id = :parentId")
    suspend fun getCalvesByParentId(parentId: String?): List<CalfEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalf(calf: CalfEntity)

    @Update
    suspend fun updateCalf(calf: CalfEntity)

    @Query("SELECT * FROM calves WHERE pendingSync = 1")
    suspend fun getPendingSyncCalves(): List<CalfEntity>

    @Query("DELETE FROM calves")
    suspend fun deleteAllCalves()
}
