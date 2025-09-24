package com.example.cattletrackingapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cattletrackingapp.ui.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.SearchByName,
        Screen.Home,
        Screen.SearchByRFID
    )

    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(Icons.Default.Home, contentDescription = "Home")
                        Screen.SearchByName -> Icon(Icons.Default.Search, contentDescription = "Search by Name")
                        Screen.SearchByRFID -> Icon(Icons.Default.Info, contentDescription = "Search by RFID")
                        else -> Icon(Icons.Default.Info, contentDescription = null)
                    }
                },
                label = { Text(screen.route.replace("_", " ").replaceFirstChar { it.uppercase() }) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
