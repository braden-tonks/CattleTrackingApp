package com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail

import com.example.cattletrackingapp.data.remote.Models.Cow
import java.util.Calendar

//This page is the translator layer between the Supabase data and Compose UI
data class CowUi(
    val id: String,
    val farmerId: String,
    val tagNumber: String,
    val damNumber: String,
    val sireNumber: String,
    val birthDate: String,
    val age: String,
    val remarks: String,
    val createdAt: String,
    val isActive: Boolean = true
)

fun Cow.toUi(): CowUi = CowUi(
    id = id ?: "",
    farmerId = farmer_id ?: "",
    tagNumber = tag_number,
    damNumber = dam_number ?: "Unknown",  // null → "Unknown"
    sireNumber = sire_number ?: "Unknown",
    birthDate = birth_date,
    age = computeAge(birth_date),
    remarks = remarks ?: "", // null → ""
    createdAt = created_at ?: "",          // null → ""
    isActive = is_active ?: true
)

fun CowUi.toModel(): Cow = Cow(
    id = id,
    farmer_id = farmerId,
    tag_number = tagNumber,
    dam_number = damNumber,
    sire_number = sireNumber,
    birth_date = birthDate,
    remarks = remarks,
    created_at = createdAt,
    is_active = isActive
)


fun computeAge(birthDate: String): String {
    if (birthDate.isNullOrBlank()) return "Unknown"

    return try {
        val parts = birthDate.split("-")
        val birthYear = parts[0].toInt()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = currentYear - birthYear
        when {
            age < 0 -> "Unknown"
            age == 1 -> "1 year"
            else -> "$age years"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

