package com.example.cattletrackingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.ui.components.NFCReaderComponent

@Composable
fun SearchByRFIDScreen() {
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
        Text("Tag Data: $tagData")
        Spacer(modifier = Modifier.height(16.dp))
        NFCReaderComponent(
            lifecycleOwner = lifecycleOwner,
            tagData = tagData,
            onStartScan = { /* nothing */ },
            onStopScan = { /* nothing */ }
        )
    }
}
