package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cows")
data class CowEntity(
    @PrimaryKey val id: String,
    val farmer_id: String,
    val tag_number: String,
    val dam_number: String? = null,
    val sire_number: String? = null,
    val birth_date: String,
    val remarks: String? = null,
    val created_at: String? = null,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false
)