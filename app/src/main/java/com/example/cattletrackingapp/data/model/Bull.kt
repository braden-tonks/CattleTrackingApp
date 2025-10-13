package com.example.cattletrackingapp.data.model

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
    val created_at: String? = null
)