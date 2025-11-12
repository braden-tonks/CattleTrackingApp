package com.example.cattletrackingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weights")
data class WeightEntity (
    @PrimaryKey val id: String,
    val cow_id: String? = null,
    val calf_id: String? = null,
    val bull_id: String? = null,
    val weight: Double,
    val date_weighed: String,
    val created_at: String? = null
)