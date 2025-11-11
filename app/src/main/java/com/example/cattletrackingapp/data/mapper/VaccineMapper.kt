package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.VaccineEntity
import com.example.cattletrackingapp.data.remote.Models.Vaccine

fun Vaccine.toEntity(): VaccineEntity = VaccineEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    farmer_id = farmer_id,
    vaccine_number = vaccine_number,
    name = name,
    description = description,
    notes = notes,
    created_at = created_at
)

fun VaccineEntity.toDto(): Vaccine = Vaccine(
    id = id,
    farmer_id = farmer_id,
    vaccine_number = vaccine_number,
    name = name,
    description = description,
    notes = notes,
    created_at = created_at
)
