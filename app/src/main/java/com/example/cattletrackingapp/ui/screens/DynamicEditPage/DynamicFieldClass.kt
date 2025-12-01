package com.example.cattletrackingapp.ui.screens.DynamicEditPage

sealed class DynamicFieldType {
    object Text : DynamicFieldType()
    object Number : DynamicFieldType()
    object Date : DynamicFieldType()
    data class Picklist(val options: List<String>) : DynamicFieldType()

    data class SearchableDropdownField(val options: List<String>) : DynamicFieldType()
}

data class DynamicField(
    val key: String,
    val label: String,
    val type: DynamicFieldType,
    val initialValue: Any?
)