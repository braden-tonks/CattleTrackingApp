package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.WeightEntity
import com.example.cattletrackingapp.data.remote.Models.Weight

fun Weight.toEntity(): WeightEntity = WeightEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    cow_id = cow_id,
    calf_id = calf_id,
    bull_id = bull_id,
    weight = weight,
    date_weighed = date_weighed, // keep name consistent with entity
    created_at = created_at
)

fun WeightEntity.toDto(): Weight = Weight(
    id = id,
    cow_id = cow_id,
    calf_id = calf_id,
    bull_id = bull_id,
    weight = weight,
    date_weighed = date_weighed,
)