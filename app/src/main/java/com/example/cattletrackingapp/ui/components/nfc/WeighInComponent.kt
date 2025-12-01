package com.example.cattletrackingapp.ui.components.nfc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cattletrackingapp.data.remote.Models.Calf

/**
 * Reusable dialog component for NFC-based calf weigh-in.
 *
 * Parameters:
 * - showDialog: controls visibility (you manage this)
 * - tagData: passed in from NFC read (pass "No tag scanned yet" or blank initially)
 * - onStartScan / onStopScan: callbacks to control NFC scanning lifecycle
 * - onClose: called when dialog should be closed
 *
 * Behavior:
 * - Blank prompt on first open.
 * - When a tag is scanned (non-blank and != "No tag scanned yet") -> loads calf.
 * - Shows last weight, input box for new weight.
 * - Submits using WeighInNfcViewModel.submitWeight -> mirrors AddWeight logic.
 * - On success, shows "Weigh Another" or "Close".
 */
@Composable
fun WeighInNfcDialog(
    showDialog: Boolean,
    tagData: String,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onClose: () -> Unit,
    viewModel: WeighInNfcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    // Load calf when tagData changes
    LaunchedEffect(tagData) {
        if (tagData.isNotBlank() && tagData != "No tag scanned yet") {
            viewModel.loadCalfByTag(tagData)
        }
    }

    if (!showDialog) return

    Dialog(onDismissRequest = {
        viewModel.resetSuccessAndForm()
        onStopScan()
        onClose()
    }) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 6.dp) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Initial blank state: no calf loaded, not loading
                if (uiState.calf == null && !uiState.isLoading && !uiState.success) {
                    Text("Scan a calf tag", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { onStartScan() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Start Scan")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.resetSuccessAndForm()
                        onStopScan()
                        onClose()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Close")
                    }
                    return@Surface
                }

                // Loading state
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                }

                // Error
                uiState.error?.let { err ->
                    Text("Error: $err", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.resetSuccessAndForm() }) {
                        Text("Okay")
                    }
                    return@Surface
                }

                // Success after submit
                if (uiState.success) {
                    Text("Weight saved!", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            // weigh another: reset and start scan
                            input = ""
                            viewModel.resetSuccessAndForm()
                            onStartScan()
                        }, modifier = Modifier.weight(1f)) {
                            Text("Weigh Another")
                        }
                        Button(onClick = {
                            viewModel.resetSuccessAndForm()
                            onStopScan()
                            onClose()
                        }, modifier = Modifier.weight(1f)) {
                            Text("Close")
                        }
                    }
                    return@Surface
                }

                // Calf loaded UI
                val calf: Calf = uiState.calf ?: return@Surface

                Text("Calf #${calf.tag_number}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Last weight: ${calf.current_weight?.toString() ?: "N/A"} lb", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { new -> input = new },
                    label = { Text("New weight (lb)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Submit + Cancel
                Button(
                    onClick = {
                        val parsed = input.toDoubleOrNull()
                        if (parsed != null) {
                            viewModel.submitWeight(parsed)
                        }
                    },
                    enabled = !uiState.submitLoading && input.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.submitLoading) "Submitting..." else "Submit")
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    // Cancel: reset and stop scan (if user wants)
                    viewModel.resetSuccessAndForm()
                    onStopScan()
                    onClose()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
            }
        }
    }
}