package com.example.cattletrackingapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Bull (
    val id: String,
    val farmer_id: String,
    val tag_number: String,
    val bull_name: String,
    val date_in: String,
    val date_out: String,
    val remarks: String,
    val created_at: String
)