package com.example.cattletrackingapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CowVaccine (
    val id: String,
    val cow_id: String,
    val calf_id: String,
    val bull_id: String,
    val vaccine_id: String,
    val date_given: String,
    val dose: String,
    val remarks: String
)