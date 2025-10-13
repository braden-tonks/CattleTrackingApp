package com.example.cattletrackingapp.ui.screens.AddBull

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.ui.components.DatePickerField

@Composable
fun AddBullScreen(navController: NavController) {
    // State for form fields
    var tagNumber by remember { mutableStateOf("") }
    var tagNumberError by remember { mutableStateOf<String?>(null) }
    var bullName by remember { mutableStateOf("") }
    var dateIn by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val viewModel: AddBullViewModel = hiltViewModel()
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Add New Bull", style = MaterialTheme.typography.headlineSmall)

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

        // Bull Name field
        OutlinedTextField(
            value = bullName,
            onValueChange = { bullName = it },
            label = { Text("Bull Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // birthDate date picker field
        DatePickerField(
            label = "Date In",
            selectedDate = dateIn,
            onDateSelected = { dateIn = it }
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
                    val bull = Bull(
                        farmer_id = "c56b4b3c-ab3b-4782-80e4-3204b14ee635",
                        tag_number = tagNumber,
                        bull_name = bullName.ifBlank { null },
                        date_in = dateIn.ifBlank { null },
                        remarks = remarks.ifBlank { null }
                    )
                    viewModel.saveBull(bull)
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
                Text("Save Cattle")
            }
        }
    }
}