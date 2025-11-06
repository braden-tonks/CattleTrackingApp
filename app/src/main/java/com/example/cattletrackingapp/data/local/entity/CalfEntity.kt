package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calves")
data class CalfEntity(
    @PrimaryKey val id: String,
    val farmer_id: String,
    val cow_id: String? = null,
    val bull_id: String? = null,
    val tag_number: String,
    val dam_number: String,
    val sire_number: String,
    val birth_date: String,
    val sex: String,
    val current_weight: Double? = null,
    val avg_gain: Double? = null,
    val remarks: String? = null,
    val created_at: String? = null,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false
)