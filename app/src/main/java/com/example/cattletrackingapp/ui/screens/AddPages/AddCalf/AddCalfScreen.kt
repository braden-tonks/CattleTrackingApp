package com.example.cattletrackingapp.ui.screens.AddPages.AddCalf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.ui.components.DatePickerField
import com.example.cattletrackingapp.ui.components.PicklistField
import com.example.cattletrackingapp.ui.components.SearchableDropdownField

@Composable
fun AddCalfScreen(navController: NavController) {
    // State for form fields
    var tagNumber by remember { mutableStateOf("") }
    var tagNumberError by remember { mutableStateOf<String?>(null) }
    var cowId by remember { mutableStateOf("") }
    var damNumber by remember { mutableStateOf("") }
    var damNumberError by remember { mutableStateOf<String?>(null) }
    var bullId by remember { mutableStateOf("") }
    var sireNumber by remember { mutableStateOf("") }
    var sireNumberError by remember { mutableStateOf<String?>(null) }
    var birthDate by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var sex by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val viewModel: AddCalfViewModel = hiltViewModel()
    val saveState = viewModel.saveState // observe ViewModel result


    fun validateForm(): Boolean {
        var valid = true

        if (tagNumber.isBlank()) {
            tagNumberError = "Tag number is required"
            valid = false
        }

        if (birthDate.isBlank()) {
            birthDateError = "Birth date is required"
            valid = false
        }

        return valid
    }


    LaunchedEffect(saveState.success) {
        if (saveState.success == true) {
            navController.popBackStack() // goes back to cattle list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Add New Calf", style = MaterialTheme.typography.headlineSmall)

        // tagNumber field
        OutlinedTextField(
            value = tagNumber,
            onValueChange = {
                tagNumber = it
                tagNumberError = null // reset error while typing
            },
            label = { Text("Tag Number") },
            isError = tagNumberError != null
        )

        if (tagNumberError != null) {
            Text(
                text = tagNumberError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        PicklistField(
            label = "Sex",
            options = listOf("Male", "Female"),
            selectedOption = sex,
            onOptionSelected = { sex = it }
        )

        // Dam Number
        SearchableDropdownField(
            label = "Dam (Cow Tag)",
            options = viewModel.cowTags,
            optionLabel = { it.tag_number },
            selectedLabel = damNumber,
            onOptionSelected = {
                damNumber = it.tag_number
                cowId = it.id
            }
        )

        // Sire Number
        SearchableDropdownField(
            label = "Sire (Bull Tag)",
            options = viewModel.bullTags,
            optionLabel = { it.tag_number },
            selectedLabel = sireNumber,
            onOptionSelected = {
                sireNumber = it.tag_number
                bullId = it.id
            }
        )

        // birthDate date picker field
        DatePickerField(
            label = "Birth Date",
            selectedDate = birthDate,
            onDateSelected = { birthDate = it }
        )

        // Remarks field
        OutlinedTextField(
            value = remarks,
            onValueChange = { remarks = it },
            label = { Text("Remarks") },
            modifier = Modifier.fillMaxWidth()
        )

        // Submit button
        Button(
            onClick = {
                if (validateForm()) {
                    val calf = Calf(
                        farmer_id = "c56b4b3c-ab3b-4782-80e4-3204b14ee635",
                        tag_number = tagNumber,
                        cow_id = cowId,
                        bull_id = bullId,
                        dam_number = damNumber,
                        sire_number = sireNumber,
                        birth_date = birthDate,
                        sex = sex,
                        remarks = remarks.ifBlank { null }
                    )
                    viewModel.saveCalf(calf)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saveState.loading
        ) {
            if (saveState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save Calf")
            }
        }
    }
}