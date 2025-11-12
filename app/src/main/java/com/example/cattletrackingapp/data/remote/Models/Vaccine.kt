// data/model/Vaccine.kt
package com.example.cattletrackingapp.data.remote.Models

import kotlinx.serialization.Serializable

@Serializable
data class Vaccine(
    val id: String? = null,
    val farmer_id: String? = null,
    val vaccine_number: Int? = null,
    val name: String,
    val description: String? = null,
    val notes: String? = null,
    val created_at: String? = null
)
