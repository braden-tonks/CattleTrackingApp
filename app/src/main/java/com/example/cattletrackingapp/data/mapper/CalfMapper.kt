package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.CalfEntity
import com.example.cattletrackingapp.data.remote.Models.Calf

fun Calf.toEntity(): CalfEntity = CalfEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    farmer_id = farmer_id,
    cow_id = cow_id,
    bull_id = bull_id,
    tag_number = tag_number,
    dam_number = dam_number,
    sire_number = sire_number,
    birth_date = birth_date,
    sex = sex,
    current_weight = current_weight,
    avg_gain = avg_gain,
    remarks = remarks,
    created_at = created_at
)

fun CalfEntity.toDto(): Calf = Calf(
    id = id,
    farmer_id = farmer_id,
    cow_id = cow_id,
    bull_id = bull_id,
    tag_number = tag_number,
    dam_number = dam_number,
    sire_number = sire_number,
    birth_date = birth_date,
    sex = sex,
    current_weight = current_weight,
    avg_gain = avg_gain,
    remarks = remarks,
    created_at = created_at
)
