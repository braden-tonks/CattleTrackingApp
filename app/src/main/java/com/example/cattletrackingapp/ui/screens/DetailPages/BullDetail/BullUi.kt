package com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail

import com.example.cattletrackingapp.data.remote.Models.Bull

//This page is the translator layer between the Supabase data and Compose UI
data class BullUi(
    val id: String,
    val tagNumber: String,
    val bullName: String?,
    val dateIn: String?,
    val dateOut: String?,
    val remarks: String,
    val createdAt: String
)

fun Bull.toUi(): BullUi = BullUi(
    id = id ?: "",
    tagNumber = tag_number,
    bullName = bull_name,
    dateIn = date_in,
    dateOut = date_out,
    remarks = remarks ?: "",
    createdAt = created_at ?: ""
)