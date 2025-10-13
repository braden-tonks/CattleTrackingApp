package com.example.cattletrackingapp.ui.screens.cowdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.ui.UIModel.CowUi
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowDetailScreen(
    navController: NavController,
    cowId: String,
    vm: CowDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(cowId) { vm.load(cowId) }
    val state = vm.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cow Details") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Go straight back to the list even if user didn’t come from it
                            navController.popBackStack(
                                com.example.cattletrackingapp.ui.navigation.Screen.CattleList.route,
                                inclusive = false
                            )
                        }
                    ) {
                        // Use automirrored back arrow if available; else fallback
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { inner ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text(state.error!!)
            }
            state.cow != null -> Box(Modifier.fillMaxSize().padding(inner)) {
                CowDetailContent(state.cow!!)
            }
        }
    }
}


@Composable
private fun CowDetailContent(cow: CowUi) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tag #${cow.tagNumber}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LabeledValue("Birth date", cow.birthDate)
                LabeledValue("Dam #", cow.damNumber)
                LabeledValue("Sire #", cow.sireNumber)
                if (cow.remarks.isNotBlank()) LabeledValue("Remarks", cow.remarks)
                if (cow.createdAt.isNotBlank()) LabeledValue("Created at", cow.createdAt)
            }
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
