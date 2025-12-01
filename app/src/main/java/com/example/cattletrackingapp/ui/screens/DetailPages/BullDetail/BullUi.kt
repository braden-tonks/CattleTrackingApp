package com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail

import com.example.cattletrackingapp.data.remote.Models.Bull

//This page is the translator layer between the Supabase data and Compose UI
data class BullUi(
    val id: String,
    val farmerId: String,
    val tagNumber: String,
    val bullName: String?,
    val dateIn: String?,
    val dateOut: String?,
    val remarks: String,
    val createdAt: String,
    val isActive: Boolean = true
)

fun Bull.toUi(): BullUi = BullUi(
    id = id ?: "",
    farmerId = farmer_id ?: "",
    tagNumber = tag_number,
    bullName = bull_name,
    dateIn = date_in,
    dateOut = date_out,
    remarks = remarks ?: "",
    createdAt = created_at ?: "",
    isActive = is_active ?: true
)

fun BullUi.toModel(): Bull = Bull(
    id = id,
    farmer_id = farmerId,
    tag_number = tagNumber,
    bull_name = bullName,
    date_in = dateIn,
    date_out = dateOut,
    remarks = remarks,
    created_at = createdAt,
    is_active = isActive
)