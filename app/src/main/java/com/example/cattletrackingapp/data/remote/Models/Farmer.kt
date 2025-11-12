package com.example.cattletrackingapp.data.remote.Models

import kotlinx.serialization.Serializable

@Serializable
data class Farmer(
    val id: String? = null,
    val email: String,
    val name: String,
    val created_at: String
)