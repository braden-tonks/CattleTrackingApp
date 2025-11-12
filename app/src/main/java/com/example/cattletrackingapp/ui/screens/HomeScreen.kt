package com.example.cattletrackingapp.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.components.HomePageComponents.DashboardScreen
import com.example.cattletrackingapp.ui.components.HomePageComponents.MenuItem
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    data class MenuItem(
        val label: String,
        val route: String,
        val icon: ImageVector
    )

    val menuItems = listOf(
        MenuItem("Add Cattle", Screen.ChooseAddCattle.route, Icons.Default.Add),
        MenuItem("Herd List", Screen.HerdList.route, Icons.AutoMirrored.Filled.List),
        MenuItem("Vaccination Module", Screen.Vaccinations.route, Icons.Filled.Vaccines),
        MenuItem(
            "Calf Weights",
            Screen.WeightModule.route,
            ImageVector.vectorResource(id = R.drawable.dashboardicon)
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Menu buttons (bottom half
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 16.dp)
                ) {
                    Text(
                        text = "Welcome John!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold

                    )
                }
            }
            item { DashboardScreen() }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            items(menuItems) { item ->
                MenuItem(item.label, item.route, item.icon, navController)
            }


        }
    }
}


