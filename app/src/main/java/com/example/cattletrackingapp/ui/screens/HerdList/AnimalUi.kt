package com.example.cattletrackingapp.ui.screens.HerdList

import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.ui.components.CattleType

data class AnimalUi(
    val id: String? = null,
    val tagNumber: String,
    val type: CattleType,
    val sex: String? = null
)

fun Cow.toAnimalUi(): AnimalUi {
    return AnimalUi(
        id = this.id,
        tagNumber = this.tag_number,
        type = CattleType.COW,
        sex = null
    )
}


fun Bull.toAnimalUi(): AnimalUi {
    return AnimalUi(
        id = this.id,
        tagNumber = this.tag_number,
        type = CattleType.BULL,
        sex = null
    )
}


fun Calf.toAnimalUi(): AnimalUi {
    return AnimalUi(
        id = this.id,
        tagNumber = this.tag_number,
        type = CattleType.CALF,
        sex = sex
    )
}