package com.example.cattletrackingapp.ui.screens.WeightModule.addweight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.ui.components.CalfWeightCard
import com.example.cattletrackingapp.ui.components.nfc.WeighInNfcDialog

@Composable
fun AddWeightScreen(navController: NavController) {
    val viewModel: AddWeightViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var showNfcDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as MainActivity
    val tagData = activity.nfcTagData


    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading calves...")
            }
            return@Box
        }

        uiState.error?.let { err ->
            Column {
                Text("Error: $err")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { showNfcDialog = true }) {
                        Text("Weigh Using NFC")
                    }
                }
            }
            item {
                Text("Calves not weighed today",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
            items(uiState.calvesNotWeighedToday) { calf ->
                CalfCard(calf = calf, onClick = { viewModel.onCalfClicked(calf) })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Calves weighed today",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
            items(uiState.calvesWeighedToday) { calf ->
                CalfCard(calf = calf, onClick = { viewModel.onCalfClicked(calf) })
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        WeighInNfcDialog(
            showDialog = showNfcDialog,
            tagData = tagData, // e.g., activity.nfcTagData
            onStartScan = { /* optional */ },
            onStopScan = { /* optional */ },
            onClose = { showNfcDialog = false }
        )


        // Dialog
        val dialogCalf = uiState.dialogCalf

        if (uiState.dialogVisible && dialogCalf != null) {
            WeightEntryDialog(
                calf = dialogCalf,
                isSubmitting = uiState.submitLoading,
                onCancel = { viewModel.dismissDialog() },
                onSubmit = { weight -> viewModel.submitWeight(weight) }
            )
        }

    }
}

@Composable
private fun CalfCard(calf: Calf, onClick: () -> Unit) {
    CalfWeightCard(
        title = "#${calf.tag_number}",
        sex =  calf.sex,
        subtitle = "${calf.current_weight ?: 0.0} lb",
        iconPainter = painterResource(R.drawable.cow_icon),
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightEntryDialog(
    calf: Calf,
    isSubmitting: Boolean,
    onCancel: () -> Unit,
    onSubmit: (Double) -> Unit
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = input.toDoubleOrNull()
                    if (parsed != null) onSubmit(parsed)
                },
                enabled = !isSubmitting
            ) {
                Text(if (isSubmitting) "Submitting..." else "Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel, enabled = !isSubmitting) {
                Text("Cancel")
            }
        },
        title = { Text("Enter weight for ${calf.tag_number}") },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { new -> input = new },
                    label = { Text("Weight (lb)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Current weight: ${calf.current_weight?.toString() ?: "N/A"}")
            }
        }
    )
}
