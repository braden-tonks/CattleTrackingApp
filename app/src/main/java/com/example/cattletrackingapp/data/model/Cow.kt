package com.example.cattletrackingapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Cow (
    val id: String,
    val farmer_id: String,
    val tag_number: String,
    val dam_number: String,
    val sire_number: String,
    val birth_date: String,
    val remarks: String,
    val created_at: String
)