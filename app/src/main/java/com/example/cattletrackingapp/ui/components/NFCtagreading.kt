package com.example.cattletrackingapp.ui.components

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun NFCReaderComponent(
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
    tagData: String,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    val viewModel: NFCViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // When NFC tag data changes, trigger load
    LaunchedEffect(tagData) {
        if (tagData != "No tag scanned yet" && tagData.isNotBlank()) {
            viewModel.loadCalves(tagData)
        } else {
            viewModel.clearState()
        }
    }

    // --- Scan Button ---
    Button(
        onClick = {
            showDialog = true
            onStartScan()
        }
    ) {
        Text("Scan NFC Tag")
    }

    // --- Dialog Overlay ---
    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            onStopScan()
        }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("Hold your phone near an NFC tag", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                    }

                    if (uiState.error != null) {
                        Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    }

                    if (uiState.calves.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                //.fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.calves) { calf ->
                                NFCcard(
                                    title = "#${calf.tag_number}",
                                    sex = calf.sex,
                                    type = "calf",
                                    iconPainter = painterResource(R.drawable.cow_icon),
                                    onClick = {
                                        navController.navigate(Screen.CalfDetail.routeWithId(calf.id))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        showDialog = false
                        onStopScan()
                    }) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // --- Lifecycle cleanup ---
    val observer = remember(lifecycleOwner) {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onStopScan()
            }
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * NFC tag intent handler — called from MainActivity.onNewIntent()
 */
fun handleNfcIntent(intent: Intent, onTagRead: (String) -> Unit, activity: Activity) {
    val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
    var tagData = ""

    if (rawMsgs != null) {
        val msgs = rawMsgs.map { it as NdefMessage }
        for (msg in msgs) {
            for (record in msg.records) {
                // Look for MIME media record with "text/plain"
                if (record.tnf == NdefRecord.TNF_MIME_MEDIA &&
                    record.type.contentEquals("text/plain".toByteArray())
                ) {
                    try {
                        // Properly decode the byte array payload
                        tagData = String(record.payload, Charsets.UTF_8).trim()
                    } catch (e: Exception) {
                        Toast.makeText(activity, "Error decoding NFC payload: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val finalTagData = if (tagData.isNotEmpty()) tagData else "No data found on tag"
    onTagRead(finalTagData)
    Toast.makeText(activity, "NFC Tag Read: $finalTagData", Toast.LENGTH_SHORT).show()
}