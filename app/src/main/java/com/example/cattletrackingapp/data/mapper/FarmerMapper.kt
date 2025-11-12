package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.FarmerEntity
import com.example.cattletrackingapp.data.remote.Models.Farmer

fun Farmer.toEntity(): FarmerEntity = FarmerEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    email = email,
    name = name,
    created_at = created_at
)

fun FarmerEntity.toDto(): Farmer = Farmer(
    id = id,
    email = email,
    name = name,
    created_at = created_at
)
