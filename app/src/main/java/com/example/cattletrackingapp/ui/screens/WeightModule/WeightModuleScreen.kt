package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cattletrackingapp.ui.components.DashboardList
import com.example.cattletrackingapp.ui.components.ListHeader
import com.example.cattletrackingapp.ui.components.WeightModuleTabs
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

@Composable
fun WeightModuleScreen(navController: NavController) {

    var selectedType by remember { mutableStateOf(DashboardList.DASHBOARD) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Tabs at the top
        WeightModuleTabs(
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )

        Spacer(Modifier.height(12.dp))

        // Content below header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedType) {

                DashboardList.DASHBOARD -> {
                    // Dashboard screen
                    DashBoardScreen(navController = navController)
                }

                DashboardList.LIST -> {
                    // Show the WeightListScreen (calves list)
                    WeightListScreen(navController = navController)
                }
            }
        }
    }
}
