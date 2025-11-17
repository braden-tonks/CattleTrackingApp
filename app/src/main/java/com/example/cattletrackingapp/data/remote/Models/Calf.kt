package com.example.cattletrackingapp.data.remote.Models

import kotlinx.serialization.Serializable

@Serializable
data class Calf (
    val id: String? = null,
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
    val created_at: String? = null
)

@Serializable
data class CowIdAndTag(
    val id: String,
    val tag_number: String
)