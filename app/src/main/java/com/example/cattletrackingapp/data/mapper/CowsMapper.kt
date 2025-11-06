package com.example.cattletrackingapp.data.mapper

import com.example.cattletrackingapp.data.local.entity.CowEntity
import com.example.cattletrackingapp.data.remote.Models.Cow

fun Cow.toEntity(): CowEntity = CowEntity(
    id = id ?: java.util.UUID.randomUUID().toString(),
    farmer_id = farmer_id,
    tag_number = tag_number,
    dam_number = dam_number,
    sire_number = sire_number,
    birth_date = birth_date,
    remarks = remarks,
    created_at = created_at
)

fun CowEntity.toDto(): Cow = Cow(
    id = id,
    farmer_id = farmer_id,
    tag_number = tag_number,
    dam_number = dam_number,
    sire_number = sire_number,
    birth_date = birth_date,
    remarks = remarks,
    created_at = created_at
)
