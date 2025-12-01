//Created by Nick Heislen 10/14/2025
package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

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
import com.example.cattletrackingapp.data.remote.Models.Bull
import com.example.cattletrackingapp.data.remote.Models.Cow
import com.example.cattletrackingapp.ui.components.DetailHeader
import com.example.cattletrackingapp.ui.components.DetailTabRow
import com.example.cattletrackingapp.ui.components.InfoRow
import com.example.cattletrackingapp.ui.components.VaccineListSection
import com.example.cattletrackingapp.ui.components.WeightListSection
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicEditScreen
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicField
import com.example.cattletrackingapp.ui.screens.DynamicEditPage.DynamicFieldType

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
                    println("SyncDebug: CalfDetailScreen: ${state.weightList}")
                    when (selectedTab) {
                        0 -> CalfDetailsSection(
                            calf = state.calf!!,
                            onEditSubmit = { updatedCalfUi ->
                                vm.updateCalf(updatedCalfUi)
                            }
                        )
                        1 -> VaccineListSection(state.cowVaccineList, navController)
                        2 -> WeightListSection(state.weightList, navController)
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
fun CalfDetailsSection(
    calf: CalfUi,
    onEditSubmit: (CalfUi) -> Unit,
    vm: CalfDetailViewModel = hiltViewModel(),
) {

    var showEditSheet by remember { mutableStateOf(false) }

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
            InfoRow(label = "Active:", value = if (calf.isActive) "Yes" else "No")
        }

        // Edit button at the bottom
        item {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { showEditSheet = true },
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Edit Calf")
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
                title = "Edit Calf",
                fields = listOf(
                    DynamicField("tagNumber", "Tag Number", DynamicFieldType.Text, calf.tagNumber),
                    DynamicField("birthDate", "Date of Birth", DynamicFieldType.Date, calf.birthDate),
                    DynamicField("sex", "Sex", DynamicFieldType.Picklist(listOf("Male", "Female")), calf.sex),
                    DynamicField("sireNumber", "Sire Number", DynamicFieldType.SearchableDropdownField(vm.bulls.map { it.tag_number }), calf.sireNumber),
                    DynamicField("damNumber", "Dam Number", DynamicFieldType.SearchableDropdownField(vm.cows.map { it.tag_number }), calf.damNumber),
                    DynamicField("remarks", "Remarks", DynamicFieldType.Text, calf.remarks),
                    DynamicField("is_active", "Active", DynamicFieldType.Picklist(listOf("Yes", "No")), if (calf.isActive) "Yes" else "No")
                    ),
                onSubmit = { values: Map<String, Any?> ->
                    val updatedCalf = calf.copy(
                        tagNumber = values["tagNumber"] as? String ?: calf.tagNumber,
                        birthDate = values["birthDate"] as? String ?: calf.birthDate,
                        sex = values["sex"] as? String ?: calf.sex,
                        sireNumber = values["sireNumber"] as? String ?: calf.sireNumber,
                        damNumber = values["damNumber"] as? String ?: calf.damNumber,
                        remarks = values["remarks"] as? String ?: calf.remarks,
                        isActive = when (values["is_active"] as? String) {
                            "Yes" -> true
                            "No" -> false
                            else -> calf.isActive
                        }
                    )

                    onEditSubmit(updatedCalf)
                    showEditSheet = false
                }
            )
        }
    }
}






