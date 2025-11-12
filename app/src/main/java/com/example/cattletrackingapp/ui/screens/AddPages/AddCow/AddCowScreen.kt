package com.example.cattletrackingapp.ui.screens.AddPages.AddCow

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
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.ui.components.DatePickerField

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

        // More rules: numeric only, length limits, etc.

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
        Text(text = "Add New Cow", style = MaterialTheme.typography.headlineSmall)

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
                Text("Save Cow")
            }
        }
    }
}
