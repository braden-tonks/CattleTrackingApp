package com.example.cattletrackingapp.data.remote.Models

import kotlinx.serialization.Serializable

@Serializable
data class Weight (
    val id: String? = null,
    val cow_id: String? = null,
    val calf_id: String? = null,
    val bull_id: String? = null,
    val weight: Double,
    val date_weighed: String,
    val created_at: String? = null

)