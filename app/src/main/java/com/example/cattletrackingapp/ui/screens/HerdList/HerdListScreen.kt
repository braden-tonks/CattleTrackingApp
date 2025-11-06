package com.example.cattletrackingapp.ui.screens.HerdList


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.components.CattleCard
import com.example.cattletrackingapp.ui.components.CattleList
import com.example.cattletrackingapp.ui.components.CattleType
import com.example.cattletrackingapp.ui.components.CattleTypeTabs
import com.example.cattletrackingapp.ui.components.ListHeader
import com.example.cattletrackingapp.ui.navigation.Screen
import com.example.compose.snippets.components.CustomizableSearchBar
import com.example.compose.snippets.components.SearchBehavior

@Composable
fun HerdListScreen(navController: NavController) {
    val viewModel: HerdListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var selectedType by remember { mutableStateOf(CattleType.ALL) }

    //This is for the search bar to filter the list
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(selectedType) {
        viewModel.filterByType(selectedType)
    }

    //list all the animals by default but filter them if the user searches a value
    val filteredAnimals = if (query.isBlank()) {
        uiState.animals
    } else {
        uiState.animals.filter { it.tagNumber.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        CattleTypeTabs(
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

            CustomizableSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {},
                searchResults = filteredAnimals,
                onResultClick = {},
                searchBehavior = SearchBehavior.Inline
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.error != null -> Text("Error: ${uiState.error}")
                    filteredAnimals.isEmpty() -> Text(
                        if (query.isNotBlank()) "No matches found."
                        else "No cattle found.",
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> CattleList(
                        items = filteredAnimals,
                        onClick = { animal ->
                            val route = when (animal.type) {
                                CattleType.COW -> Screen.CowDetail.routeWithId(animal.id)
                                CattleType.CALF -> Screen.CalfDetail.routeWithId(animal.id)
                                CattleType.BULL -> Screen.BullDetail.routeWithId(animal.id)
                                else -> null
                            }
                            route?.let { navController.navigate(it) }
                        },
                        itemContent = { animal, onClick ->
                            CattleCard(
                                title = "${animal.tagNumber}",
                                subtitle = "${animal.type.singularName}${animal.sex?.let { " - $it" } ?: ""}",
                                iconPainter = painterResource(R.drawable.cow_icon),
                                onClick = onClick
                            )
                        }
                    )
                }
            }
        }
    }
}