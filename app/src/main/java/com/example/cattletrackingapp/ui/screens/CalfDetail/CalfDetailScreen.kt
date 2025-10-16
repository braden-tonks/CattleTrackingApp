//Created by Nick Heislen 10/14/2025
package com.example.cattletrackingapp.ui.screens.CalfDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.ui.components.InfoCards

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalfDetailScreen(
    calfId: String,
    navController: NavController
) {
    val viewModel : CalfDetailViewModel = hiltViewModel()

    LaunchedEffect(calfId) { viewModel.load(calfId) }

    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back")
            }

            // Title centered or slightly offset
            Text(
                "Calf Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.error!!)
            }

            state.calf != null -> CalfDetailContent(state.calf)
        }
    }
}

@Composable
fun CalfDetailContent(calf:Calf) {
    InfoCards(
        title = "Tag #${calf.tag_number}",
        fields = listOf(
            "Birth date" to calf.birth_date,
            "Dam #" to calf.dam_number,
            "Sire #" to calf.sire_number,
            "Sex" to calf.sex,
            "Current Weight" to calf.current_weight?.let { "$it lbs" },
            "Average Gain" to calf.avg_gain?.let { "$it. lbs "},
            "Remarks" to calf.remarks,
            "Created At" to calf.created_at,
        )
    )
}







