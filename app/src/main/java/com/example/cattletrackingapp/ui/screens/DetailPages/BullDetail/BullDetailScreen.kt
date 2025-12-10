package com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import com.example.cattletrackingapp.ui.components.CalfListSection
import com.example.cattletrackingapp.ui.components.DetailHeader
import com.example.cattletrackingapp.ui.components.DetailTabRow
import com.example.cattletrackingapp.ui.components.InfoRow
import com.example.cattletrackingapp.ui.components.VaccineListSection
import com.example.cattletrackingapp.ui.navigation.Screen
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicEditScreen
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicField
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicFieldType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BullDetailScreen(
    navController: NavController,
    bullId: String
) {
    val vm: BullDetailViewModel = hiltViewModel()

    LaunchedEffect(bullId) {
        vm.loadBullDetails(bullId)
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

        state.bull != null -> {
            // Show the cow details content
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DetailHeader(
                    iconPainter = painterResource(R.drawable.cow_detail_icon),
                    tagNumber = "Tag # ${state.bull!!.tagNumber}",
                    type = "Bull"
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
                        0 -> BullDetailsSection(
                            bull = state.bull!!,
                            onEditSubmit = { updatedBullUi ->
                                vm.updateBull(updatedBullUi)
                            }
                        )
                        1 -> VaccineListSection(state.cowVaccineList, navController)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BullDetailsSection(
    bull: BullUi,
    onEditSubmit: (BullUi) -> Unit
) {

    var showEditSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        item {
            InfoRow(label = "Tag Number:", value = bull.tagNumber)
            InfoRow(label = "Bull Name:", value = bull.bullName ?: "N/A")
            InfoRow(label = "Date In:", value = bull.dateIn ?: "N/A")
            InfoRow(label = "Date Out:", value = bull.dateOut ?: "N/A")
            InfoRow(label = "Remarks:", value = bull.remarks ?: "")
            InfoRow(label = "Active:", value = if (bull.isActive) "Yes" else "No")
        }

        // Edit button at the bottom
        item {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { showEditSheet = true },
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Edit Bull")
            }
        }
    }



    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showEditSheet = false
            }
        ) {
            DynamicEditScreen(
                title = "Edit Bull",
                fields = listOf(
                    DynamicField("tagNumber", "Tag Number", DynamicFieldType.Text, bull.tagNumber),
                    DynamicField("bullName", "Bull Name", DynamicFieldType.Text, bull.bullName),
                    DynamicField("dateIn", "Date In", DynamicFieldType.Date, bull.dateIn),
                    DynamicField("dateOut", "Date Out", DynamicFieldType.Date, bull.dateOut),
                    DynamicField("remarks", "Remarks", DynamicFieldType.Text, bull.remarks),
                    DynamicField("is_active", "Active", DynamicFieldType.Picklist(listOf("Yes", "No")), if (bull.isActive) "Yes" else "No")
                ),
                onSubmit = { values: Map<String, Any?> ->
                    val updatedBull = bull.copy(
                        tagNumber = values["tagNumber"] as? String ?: bull.tagNumber,
                        bullName = values["bullName"] as? String ?: bull.bullName,
                        dateIn = values["dateIn"] as? String ?: bull.dateIn,
                        dateOut = values["dateOut"] as? String ?: bull.dateOut,
                        remarks = values["remarks"] as? String ?: bull.remarks,
                        isActive = when (values["is_active"] as? String) {
                            "Yes" -> true
                            "No" -> false
                            else -> bull.isActive
                        }
                    )

                    onEditSubmit(updatedBull)
                    showEditSheet = false
                }
            )
        }
    }
}