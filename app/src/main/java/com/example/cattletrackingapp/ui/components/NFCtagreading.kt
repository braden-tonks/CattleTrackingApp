package com.example.cattletrackingapp.ui.components

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.nfc.Tag
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun NFCReaderComponent(
    lifecycleOwner: LifecycleOwner,
    onTagRead: (String) -> Unit
) {
    // Placeholder state just to keep Compose aware of the lifecycle
    var blank by remember { mutableStateOf(0) }

    val observer = remember(lifecycleOwner) {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Nothing needed here; onNewIntent will trigger
                blank++
            }
        }
    }

    lifecycleOwner.lifecycle.addObserver(observer)

    // Remove observer when Composable leaves composition
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Call this from MainActivity.onNewIntent
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
