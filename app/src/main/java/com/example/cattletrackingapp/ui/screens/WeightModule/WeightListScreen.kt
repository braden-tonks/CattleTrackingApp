package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.components.CalfWeightCard
import com.example.cattletrackingapp.ui.components.CattleCard
import com.example.cattletrackingapp.ui.components.ListHeader
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun WeightListScreen(navController: NavController) {

    val viewModel: WeightModuleViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCalves()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(Modifier.height(12.dp))

        // Sort toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { viewModel.toggleSortOrder() }
            ) {
                Text(
                    text = if (uiState.isDescending) "Sort: High → Low" else "Sort: Low → High"
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.calves.isEmpty() -> {
                    Text(
                        text = "No calves found.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.calves) { calf ->
                            CalfWeightCard(
                                title = "#${calf.tag_number}",
                                sex =  calf.sex,
                                subtitle = "${calf.current_weight ?: 0.0} lb",
                                iconPainter = painterResource(R.drawable.cow_icon),
                                onClick = { navController.navigate(Screen.CalfDetail.routeWithId(calf.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
