package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.components.CalfWeightCard
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun DashBoardScreen(navController: NavController){
    val viewModel: DashBoardViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCalves()
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                    // We know getData() returns: [heaviestCalf, lightestCalf]
                    val calves = uiState.calves
                    val heaviestCalf = calves.getOrNull(0)
                    val lightestCalf = calves.getOrNull(1)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (heaviestCalf != null) {
                            item {
                                Text(
                                    text = "Heaviest Calf",
                                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                CalfWeightCard(
                                    title = "#${heaviestCalf.tag_number}",
                                    sex = heaviestCalf.sex,
                                    subtitle = "${heaviestCalf.current_weight ?: 0.0} lb",
                                    iconPainter = painterResource(R.drawable.cow_icon),
                                    onClick = { navController.navigate(Screen.CalfDetail.routeWithId(heaviestCalf.id)) }
                                )
                            }
                        }

                        if (lightestCalf != null) {
                            item {
                                Text(
                                    text = "Lightest Calf",
                                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                                )
                                CalfWeightCard(
                                    title = "#${lightestCalf.tag_number}",
                                    sex = lightestCalf.sex,
                                    subtitle = "${lightestCalf.current_weight ?: 0.0} lb",
                                    iconPainter = painterResource(R.drawable.cow_icon),
                                    onClick = { navController.navigate(Screen.CalfDetail.routeWithId(lightestCalf.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}