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
import com.example.cattletrackingapp.ui.components.InfoCards


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
                title = { Text("Cow Details", style=MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, // ← Same background
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Go straight back to the list even if user didn’t come from it
                            navController.popBackStack()
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
    InfoCards(
        title = "Tag #${cow.tagNumber}",
        fields = listOf(
            "Birth date" to cow.birthDate,
            "Dam #" to cow.damNumber,
            "Sire #" to cow.sireNumber,
            "Remarks" to cow.remarks,
            "Created at" to cow.createdAt
        )
    )
}
