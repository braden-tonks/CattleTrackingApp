package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class FarmerEntity (
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val created_at: String,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false
)
