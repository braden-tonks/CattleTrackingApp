// app/src/main/java/com/example/cattletrackingapp/ui/screens/CattleListScreen.kt
package com.example.cattletrackingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.ui.cattle.CattleViewModel
import com.example.cattletrackingapp.ui.cattle.CowUi
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun CattleListScreen(navController: NavController) {
    // Get ViewModel and its observable state (cows, loading, error)
    val vm: CattleViewModel = viewModel()
    val state = vm.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header row: screen title + Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cattle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f) // push Add button to the end
            )
            FilledTonalButton(
                onClick = { navController.navigate(Screen.AddCattle.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(6.dp))
                Text("Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Decide what to show based on state
        when {
            state.loading -> {
                // Show spinner while loading
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                // Show error message + retry button
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.refresh() }) { Text("Retry") }
                    }
                }
            }
            state.cows.isEmpty() -> {
                // Show empty state message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No cattle yet")
                }
            }
            else -> {
                // Show list of cows when data is available
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.cows, key = { it.id }) { cow ->
                        CowRow(
                            cow = cow,
                            onClick = {
                                // Only navigate if you’ve defined a details route:
                                // navController.navigate("${Screen.CattleDetails.route}/${cow.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CowRow(
    cow: CowUi,
    onClick: () -> Unit
) {
    // Card for each cow entry
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // clickable for details
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column with cow details
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Tag ${cow.tagNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Dam: ${cow.damNumber}  •  Sire: ${cow.sireNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Birth: ${cow.birthDate}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (cow.remarks.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = cow.remarks,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            // Arrow icon to suggest navigation

            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}