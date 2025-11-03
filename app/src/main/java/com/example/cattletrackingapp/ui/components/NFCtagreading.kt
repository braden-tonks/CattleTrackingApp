package com.example.cattletrackingapp.ui.components

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Button that opens a MaterialDialog for NFC scanning.
 * The actual NFC reading happens through MainActivity.onNewIntent → handleNfcIntent().
 */
@Composable
fun NFCReaderComponent(
    lifecycleOwner: LifecycleOwner,
    tagData: String,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

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
                    modifier = androidx.compose.ui.Modifier
                        .padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("Hold your phone near an NFC tag", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                    if (tagData != "No tag scanned yet") {
                        Text(
                            text = tagData,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                        Button(onClick = {
                            showDialog = false
                            onStopScan()
                        }) {
                            Text("Close")
                        }
                    } else {
                        Button(onClick = {
                            showDialog = false
                            onStopScan()
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }

    // Keep NFC lifecycle cleanly attached
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
    val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
    tag?.let {
        val tagId = it.id.joinToString(":") { byte -> "%02X".format(byte) }

        var textData = ""
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMsgs != null) {
            val msgs = rawMsgs.map { it as NdefMessage }
            for (msg in msgs) {
                for (record in msg.records) {
                    val payload = record.payload
                    val text = payload.drop(1).toByteArray().decodeToString()
                    textData += text
                }
            }
        }

        val fullData = if (textData.isNotEmpty()) "ID: $tagId\nData: $textData" else "ID: $tagId"
        onTagRead(fullData)
        Toast.makeText(activity, "NFC Tag Read!", Toast.LENGTH_SHORT).show()
    }
}
