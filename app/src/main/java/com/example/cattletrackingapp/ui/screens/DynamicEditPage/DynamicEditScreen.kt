package com.example.cattletrackingapp.ui.screens.DynamicEditPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.ui.components.DatePickerField
import com.example.cattletrackingapp.ui.components.PicklistField
import com.example.cattletrackingapp.ui.components.SearchableDropdownField

@Composable
fun DynamicEditScreen(
    title: String,
    fields: List<DynamicField>,
    onSubmit: (Map<String, Any?>) -> Unit
) {
    val state = remember {
        mutableStateMapOf<String, Any?>().apply {
            fields.forEach { this[it.key] = it.initialValue }
        }
    }

    LazyColumn(Modifier.padding(16.dp)) {
        item { Text(title, style = MaterialTheme.typography.headlineSmall) }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            fields.forEach { field ->
                when (field.type) {

                    DynamicFieldType.Text -> {
                        TextField(
                            value = state[field.key]?.toString() ?: "",
                            onValueChange = { state[field.key] = it },
                            label = { Text(field.label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    DynamicFieldType.Number -> {
                        TextField(
                            value = state[field.key]?.toString() ?: "",
                            onValueChange = { state[field.key] = it.toIntOrNull() },
                            label = { Text(field.label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    DynamicFieldType.Date -> {
                        // You can plug in your existing date picker
                        DatePickerField(
                            selectedDate = state[field.key] as? String ?: "",
                            onDateSelected = { state[field.key] = it },
                            label = field.label
                        )
                    }

                    is DynamicFieldType.Picklist -> {
                        PicklistField(
                            label = field.label,
                            options = field.type.options,
                            selectedOption = state[field.key].toString(),
                            onOptionSelected = { state[field.key] = it }
                        )
                    }

                    is DynamicFieldType.SearchableDropdownField -> {
                        SearchableDropdownField(
                            label = field.label,
                            options = field.type.options,
                            optionLabel = { it },
                            selectedLabel = state[field.key].toString(),
                            onOptionSelected = { state[field.key] = it }
                        )

                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            Button(
                onClick = { onSubmit(state) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}