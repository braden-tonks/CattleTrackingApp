package com.example.cattletrackingapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.data.model.Farmer
import com.example.cattletrackingapp.data.repository.FarmersRepository

@Composable
fun FarmersScreen(
    repository: FarmersRepository = FarmersRepository()
) {
    var farmers by remember { mutableStateOf<List<Farmer>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            farmers = repository.fetchFarmers()
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    if (errorMessage != null) {
        Text("Error: $errorMessage")
    } else {
        LazyColumn {
            items(farmers, key = { it.id }) { farmer ->
                Text(farmer.name, modifier = Modifier.padding(8.dp))
            }
        }
    }
}