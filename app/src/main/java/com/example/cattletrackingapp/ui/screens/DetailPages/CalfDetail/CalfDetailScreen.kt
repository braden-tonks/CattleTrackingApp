//Created by Nick Heislen 10/14/2025
package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.components.DetailHeader
import com.example.cattletrackingapp.ui.components.DetailTabRow
import com.example.cattletrackingapp.ui.components.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalfDetailScreen(
    navController: NavController,
    calfId: String
) {
    val vm: CalfDetailViewModel = hiltViewModel()

    LaunchedEffect(calfId) {
        vm.loadCalfDetails(calfId)
    }
    val state by vm.uiState.collectAsState()

    when {
        state.loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "Error: ${state.error}",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        state.calf != null -> {
            // Show the cow details content
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DetailHeader(
                    iconPainter = painterResource(R.drawable.cow_detail_icon),
                    tagNumber = "Tag # ${state.calf!!.tagNumber}",
                    type = "Calf"
                )

                var selectedTab by remember { mutableStateOf(0) }

                DetailTabRow(
                    tabs = listOf("Details", "Vaccines", "Weights"),
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> CalfDetailsSection(state.calf!!)
                        1 -> Text("Vaccines")//VaccinesListSection(state.cowVaccineList)
                        2 -> Text("Weights")//WeightListSection(state.calfWeightList)
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "Cow details not found",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun CalfDetailsSection(calf: CalfUi) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        item {
            InfoRow(label = "Tag Number:", value = calf.tagNumber)
            InfoRow(label = "Date of Birth:", value = calf.birthDate ?: "N/A")
            InfoRow(label = "Sex:", value = calf.sex ?: "N/A")
            InfoRow(label = "Sire Number:", value = calf.sireNumber ?: "N/A")
            InfoRow(label = "Dam Number:", value = calf.damNumber ?: "N/A")
            InfoRow(label = "Current Weight:", value = calf.currentWeight?.toString() ?: "N/A")
            InfoRow(label = "Average Gain:", value = calf.avgGain?.toString() ?: "N/A")
            InfoRow(label = "Remarks:", value = calf.remarks ?: "")
        }
    }
}






