package com.example.cattletrackingapp.data.remote.Models

import kotlinx.serialization.Serializable

@Serializable
data class Bull (
    val id: String? = null,
    val farmer_id: String,
    val tag_number: String,
    val bull_name: String? = null,
    val date_in: String? = null,
    val date_out: String? = null,
    val remarks: String? = null,
    val created_at: String? = null,
    val is_active: Boolean? = null
)

@Serializable
data class BullIdAndTag(
    val id: String,
    val tag_number: String
)