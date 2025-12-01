package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

import com.example.cattletrackingapp.data.remote.Models.Calf

data class CalfUi(
    val id: String,
    val farmerId: String,
    val bullId: String,
    val cowId: String,
    val tagNumber: String,
    val damNumber: String,
    val sireNumber: String,
    val birthDate: String,
    val currentWeight: Double?,
    val avgGain: Double?,
    val sex: String,
    val remarks: String,
    val createdAt: String,
    val isActive: Boolean
)

fun Calf.toUi(): CalfUi = CalfUi(
    id = id ?: "",
    farmerId = farmer_id ?: "",
    bullId = bull_id ?: "",
    cowId = cow_id ?: "",
    tagNumber = tag_number,
    damNumber = dam_number,
    sireNumber = sire_number,
    birthDate = birth_date,
    currentWeight = current_weight ?: 0.0,
    avgGain = avg_gain ?: 0.0,
    sex = sex,
    remarks = remarks ?: "", // null → "",
    createdAt = created_at ?: "",
    isActive = is_active ?: true
)

fun CalfUi.toModel(): Calf = Calf(
    id = id,
    farmer_id = farmerId,
    bull_id = bullId,
    cow_id = cowId,
    tag_number = tagNumber,
    dam_number = damNumber,
    sire_number = sireNumber,
    birth_date = birthDate,
    current_weight = currentWeight,
    avg_gain = avgGain,
    sex = sex,
    remarks = remarks,
    created_at = createdAt,
    is_active = isActive
)