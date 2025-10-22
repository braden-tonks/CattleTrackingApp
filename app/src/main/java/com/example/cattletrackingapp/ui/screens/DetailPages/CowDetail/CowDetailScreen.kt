package com.example.cattletrackingapp.ui.screens.cowdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.CowUi
import com.example.cattletrackingapp.ui.components.CalfListSection
import com.example.cattletrackingapp.ui.components.DetailHeader
import com.example.cattletrackingapp.ui.components.DetailTabRow
import com.example.cattletrackingapp.ui.components.InfoRow
import com.example.cattletrackingapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowDetailScreen(
    navController: NavController,
    cowId: String
) {
    val vm: CowDetailViewModel = hiltViewModel()

    LaunchedEffect(cowId) {
        vm.loadCowDetails(cowId)
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

        state.cow != null -> {
            // Show the cow details content
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DetailHeader(
                    iconPainter = painterResource(R.drawable.cow_detail_icon),
                    tagNumber = "Tag # ${state.cow!!.tagNumber}",
                    type = "Cow"
                )

                var selectedTab by remember { mutableStateOf(0) }

                DetailTabRow(
                    tabs = listOf("Details", "Vaccines", "Calves"),
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> CowDetailsSection(state.cow!!)
                        1 -> Text("Vaccines")//VaccinesListSection(state.cowVaccineList)
                        2 -> CalfListSection(state.calfList, onClick = { calf ->
                            navController.navigate(Screen.CalfDetail.routeWithId(calf.id))
                        })
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
fun CowDetailsSection(cow: CowUi) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        item {
            InfoRow(label = "Tag Number:", value = cow.tagNumber)
            InfoRow(label = "Date of Birth:", value = cow.birthDate ?: "N/A")
            InfoRow(label = "Age:", value = cow.age ?: "N/A")
            InfoRow(label = "Sire Number:", value = cow.sireNumber ?: "N/A")
            InfoRow(label = "Dam Number:", value = cow.damNumber ?: "N/A")
            InfoRow(label = "Remarks:", value = cow.remarks ?: "")
        }
    }
}

