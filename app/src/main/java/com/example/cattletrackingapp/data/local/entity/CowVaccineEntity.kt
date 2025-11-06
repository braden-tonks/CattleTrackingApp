package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cow_vaccines")
data class CowVaccineEntity (
    @PrimaryKey val id: String,
    val cow_id: String,
    val calf_id: String,
    val bull_id: String,
    val vaccine_id: String,
    val date_given: String,
    val dose: String,
    val remarks: String,

    // Offline-sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = false

)
