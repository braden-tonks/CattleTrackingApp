package com.example.cattletrackingapp.ui.screens.AddCow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.ui.screens.AddCow.AddCowViewModel

@Composable
fun AddCowScreen(navController: NavController) {
    // State for form fields
    var tagNumber by remember { mutableStateOf("") }
    var tagNumberError by remember { mutableStateOf<String?>(null) }
    var damNumber by remember { mutableStateOf("") }
    var sireNumber by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var remarks by remember { mutableStateOf("") }

    val viewModel: AddCowViewModel = hiltViewModel()
    val saveState = viewModel.saveState // observe ViewModel result


    fun validateForm(): Boolean {
        var valid = true

        if (tagNumber.isBlank()) {
            tagNumberError = "Tag number is required"
            valid = false
        }

        // Example: birth date must be chosen
        if (birthDate.isBlank()) {
            birthDateError = "Please select a birth date"
            valid = false
        }

        if (birthDate.length != 10) {
            birthDateError = "Invalid date format"
            valid = false
        } else if (birthDate.substring(2, 3) != "/" || birthDate.substring(5, 6) != "/") {
            birthDateError = "Invalid date format"
            valid = false
        } else if (birthDate.substring(0, 2).toInt() > 12 || birthDate.substring(0, 2)
                .toInt() < 1
        ) {
            birthDateError = "Invalid date format"
            valid = false
        } else if (birthDate.substring(3, 5).toInt() > 31 || birthDate.substring(3, 5).toInt() < 1) {
            birthDateError = "Invalid date format"
        valid = false
        }


        // More rules: numeric only, length limits, etc.

        return valid
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Add New Cattle", style = MaterialTheme.typography.headlineSmall)

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

        // damNumber field
        OutlinedTextField(
            value = damNumber,
            onValueChange = { damNumber = it },
            label = { Text("Dam Number") },
            modifier = Modifier.fillMaxWidth()
        )

        // sireNumber field
        OutlinedTextField(
            value = sireNumber,
            onValueChange = { sireNumber = it },
            label = { Text("Sire Number") },
            modifier = Modifier.fillMaxWidth()
        )

        // DOB field (as a string for now)
        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                birthDate = it
                birthDateError = null // reset error while typing
            },
            label = { Text("Birth Date (mm/dd/yyyy)") },
            modifier = Modifier.fillMaxWidth(),
            isError = birthDateError != null
        )

        if (birthDateError != null) {
            Text(
                text = birthDateError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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
                    val cow = Cow(
                        farmer_id = "c56b4b3c-ab3b-4782-80e4-3204b14ee635",
                        tag_number = tagNumber,
                        dam_number = damNumber.ifBlank { null },
                        sire_number = sireNumber.ifBlank { null },
                        birth_date = birthDate,
                        remarks = remarks.ifBlank { null }
                    )
                    viewModel.saveCow(cow)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Cattle")
        }

        if (saveState != null) {
            Text(
                text = saveState,
                style = MaterialTheme.typography.bodyLarge,
                color = if (saveState.contains("Saved")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}
