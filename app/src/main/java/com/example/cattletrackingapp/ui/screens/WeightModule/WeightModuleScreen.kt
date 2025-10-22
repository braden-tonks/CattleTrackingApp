package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cattletrackingapp.ui.components.DashboardList
import com.example.cattletrackingapp.ui.components.ListHeader
import com.example.cattletrackingapp.ui.components.WeightModuleTabs


@Composable
fun WeightModuleScreen(navController: NavController){

    var selectedType by remember { mutableStateOf(DashboardList.DASHBOARD) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        WeightModuleTabs(
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )
        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ListHeader(
                selectedType.displayName,
                onAddClick = { navController.navigate(selectedType.addRoute) }
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}