package com.example.cattletrackingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.ui.components.nfc.NFCReaderComponent

@Composable
fun SearchByRFIDScreen(navController: NavController) {

    // All are needed for the nfc component to work
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current as MainActivity
    val tagData = activity.nfcTagData

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tag Data: $tagData") // This is how to pull the UUID from the scanned tag
        Spacer(modifier = Modifier.height(16.dp))

        // Component button call
        NFCReaderComponent(
            navController = navController,
            lifecycleOwner = lifecycleOwner,
            tagData = tagData,
            onStartScan = { /* nothing */ },
            onStopScan = { /* nothing */ }
        )
    }
}
