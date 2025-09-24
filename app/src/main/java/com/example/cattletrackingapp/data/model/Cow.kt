package com.example.cattletrackingapp.data.model

import androidx.annotation.Nullable
import kotlinx.serialization.Serializable

@Serializable
data class Cow (
    val id: String? = null,
    val farmer_id: String,
    val tag_number: String,
    val dam_number: String? = null,
    val sire_number: String? = null,
    val birth_date: String,
    val remarks: String? = null,
    val created_at: String? = null
)