package com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail

import com.example.cattletrackingapp.data.model.Cow
import java.util.Calendar

//This page is the translator layer between the Supabase data and Compose UI
data class CowUi(
    val id: String,
    val tagNumber: String,
    val damNumber: String,
    val sireNumber: String,
    val birthDate: String,
    val age: String,
    val remarks: String,
    val createdAt: String
)

fun Cow.toUi(): CowUi = CowUi(
    id = id ?: "",                        // null → ""
    tagNumber = tag_number,
    damNumber = dam_number ?: "Unknown",  // null → "Unknown"
    sireNumber = sire_number ?: "Unknown",
    birthDate = birth_date,
    age = computeAge(birth_date),
    remarks = remarks ?: "", // null → ""
    createdAt = created_at ?: ""          // null → ""
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

