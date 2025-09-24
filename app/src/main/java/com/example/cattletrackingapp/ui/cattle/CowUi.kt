package com.example.cattletrackingapp.ui.cattle

import com.example.cattletrackingapp.data.model.Cow // your Supabase model (snake_case fields)

//This page is the translator layer between the Supabase data and Compose UI
data class CowUi(
    val id: String,
    val tagNumber: String,
    val damNumber: String,
    val sireNumber: String,
    val birthDate: String,
    val remarks: String,
    val createdAt: String
)

fun Cow.toUi(): CowUi = CowUi(
    id = id ?: "",                        // null → ""
    tagNumber = tag_number,
    damNumber = dam_number ?: "Unknown",  // null → "Unknown"
    sireNumber = sire_number ?: "Unknown",
    birthDate = birth_date,
    remarks = remarks ?: "",              // null → ""
    createdAt = created_at ?: ""          // null → ""
)

