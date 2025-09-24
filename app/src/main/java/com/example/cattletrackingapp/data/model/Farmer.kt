package com.example.cattletrackingapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Farmer(
    val id: String,
    val email: String,
    val name: String,
    val created_at: String
)