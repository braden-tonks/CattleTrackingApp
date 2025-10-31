package com.example.cattletrackingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchByRFIDScreen(
    navController: NavController,
    tagData: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        item { Text("Search by RFID Screen", style = MaterialTheme.typography.titleMedium) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Text("NFC Tag Data: $tagData", style = MaterialTheme.typography.bodyMedium) }
    }
}
