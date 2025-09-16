package com.example.cattletrackingapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Calf (
    val id: String,
    val farmer_id: String,
    val cow_id: String,
    val bull_id: String,
    val tag_number: String,
    val dam_number: String,
    val sire_number: String,
    val birth_date: String,
    val sex: String,
    val current_weight: Double,
    val avg_gain: Double,
    val remarks: String,
    val created_at: String
)