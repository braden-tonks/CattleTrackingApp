package com.example.cattletrackingapp.ui.screens.SearchPage


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.ui.components.CattleType
import com.example.cattletrackingapp.ui.components.nfc.NFCReaderComponent
import com.example.cattletrackingapp.ui.navigation.Screen
import com.example.compose.snippets.components.CustomizableSearchBar

@Composable
fun SearchScreen(
    navController: NavController
) {

    val vm: SearchBarViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    // All are needed for the nfc component to work
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current as MainActivity
    val tagData = activity.nfcTagData

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        CustomizableSearchBar(
            query = query,
            onQueryChange = {
                query = it
                // Only trigger search when minimum chars are met or empty (to clear)
                vm.onQueryChange(tagNumber = it)
            },
            onSearch = {
                // Use the current query, not the lambda’s 'it'
                vm.onQueryChange(tagNumber = query)
            },
            searchResults = uiState.animals,
            onResultClick = { animal ->
                when (animal.type) {
                    CattleType.BULL -> navController.navigate(Screen.BullDetail.routeWithId(animal.id))
                    CattleType.COW -> navController.navigate(Screen.CowDetail.routeWithId(animal.id))
                    CattleType.CALF -> navController.navigate(Screen.CalfDetail.routeWithId(animal.id))
                    else -> {}
                }
            },
            supportingContent = { animal ->
                Text(text = "${animal.type.singularName}")
            },
            headlineContent = { animal ->
                Text(text = animal.tagNumber)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            query.isNotBlank() && uiState.animals.isEmpty() -> {
                Text(
                    text = "No results found",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Component button call
            NFCReaderComponent(
                navController = navController,
                lifecycleOwner = lifecycleOwner,
                tagData = tagData,
                onStartScan = { /* nothing */ },
                onStopScan = { /* nothing */ }
            )
        }
    }
}
