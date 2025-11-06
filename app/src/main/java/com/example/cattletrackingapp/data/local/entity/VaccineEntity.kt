package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines")
data class VaccineEntity (
    @PrimaryKey val id: String,
    val farmer_id: String? = null,
    val vaccine_number: Int? = null,
    val name: String,
    val description: String? = null,
    val notes: String? = null,
    val created_at: String? = null,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false
)