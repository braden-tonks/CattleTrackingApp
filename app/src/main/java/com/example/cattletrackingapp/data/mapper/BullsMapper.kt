package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.BullEntity
import com.example.cattletrackingapp.data.remote.Models.Bull

fun Bull.toEntity(): BullEntity = BullEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    farmer_id = farmer_id,
    tag_number = tag_number,
    bull_name = bull_name,
    date_in = date_in,
    date_out = date_out,
    remarks = remarks,
    created_at = created_at,
    is_active = is_active
)

fun BullEntity.toDto(): Bull = Bull(
    id = id,
    farmer_id = farmer_id,
    tag_number = tag_number,
    bull_name = bull_name,
    date_in = date_in,
    date_out = date_out,
    remarks = remarks,
    created_at = created_at,
    is_active = is_active
)
