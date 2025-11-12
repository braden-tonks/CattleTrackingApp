package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.CowVaccineEntity
import com.example.cattletrackingapp.data.remote.Models.CowVaccine

fun CowVaccine.toEntity(): CowVaccineEntity = CowVaccineEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    cow_id = cow_id,
    calf_id = calf_id,
    bull_id = bull_id,
    vaccine_id = vaccine_id,
    date_given = date_given,
    dose = dose,
    remarks = remarks
)

fun CowVaccineEntity.toDto(): CowVaccine = CowVaccine(
    id = id,
    cow_id = cow_id,
    calf_id = calf_id,
    bull_id = bull_id,
    vaccine_id = vaccine_id,
    date_given = date_given,
    dose = dose,
    remarks = remarks
)
