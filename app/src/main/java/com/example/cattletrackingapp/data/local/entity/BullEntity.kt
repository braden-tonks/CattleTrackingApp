package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bulls")
data class BullEntity (
    @PrimaryKey val id: String,
    val farmer_id: String,
    val tag_number: String,
    val bull_name: String? = null,
    val date_in: String? = null,
    val date_out: String? = null,
    val remarks: String? = null,
    val created_at: String? = null,
    val is_active: Boolean? = null,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false
)