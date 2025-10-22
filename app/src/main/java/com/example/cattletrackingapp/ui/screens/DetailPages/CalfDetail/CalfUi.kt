package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

import com.example.cattletrackingapp.data.model.Calf

data class CalfUi(
    val id: String,
    val tagNumber: String,
    val damNumber: String,
    val sireNumber: String,
    val birthDate: String,
    val currentWeight: Double?,
    val avgGain: Double?,
    val sex: String,
    val remarks: String,
    val createdAt: String
)

fun Calf.toUi(): CalfUi = CalfUi(
    id = id ?: "",                        // null → ""
    tagNumber = tag_number,
    damNumber = dam_number ?: "Unknown",  // null → "Unknown"
    sireNumber = sire_number ?: "Unknown",
    birthDate = birth_date,
    currentWeight = current_weight ?: 0.0,
    avgGain = avg_gain ?: 0.0,
    sex = sex,
    remarks = remarks ?: "", // null → "",
    createdAt = created_at ?: ""          // null → ""
)