package com.example.cattletrackingapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicklistField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // controls dropdown open/close

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {}, // read-only, selection only
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option) // update parent state
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * A reusable searchable dropdown field built with Material3's ExposedDropdownMenuBox.
 *
 * This component works like a normal dropdown, but also allows the user to
 * filter the list by typing into the text field. It is generic (`<T>`), allowing
 * you to use any data type as the dropdown option.
 *
 * PARAMETERS:
 * @param label Text label displayed above the field.
 * @param options List of selectable items of type `<T>`.
 * @param optionLabel Function that converts an item of type `<T>` into the
 *                    string displayed in the dropdown.
 * @param selectedLabel The current selection's label text. This is shown in
 *                      the text field when the dropdown is closed.
 * @param onOptionSelected Callback invoked when the user selects an item.
 *                         Receives the selected option of type `<T>`.
 **/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdownField(
    label: String,
    options: List<T>,
    optionLabel: (T) -> String,
    selectedLabel: String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(selectedLabel) }

// Filtered list (but show all if query is blank)
    val filteredOptions = remember(query, options) {
        if (query.isBlank()) options
        else options.filter { option ->
            optionLabel(option).contains(query, ignoreCase = true)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true // reopen menu while typing
            },
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 200.dp) // limits menu height
                .verticalScroll(rememberScrollState()) // adds scrollability
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No results found") },
                    onClick = { expanded = false }
                )
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onOptionSelected(option)
                            query = optionLabel(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}