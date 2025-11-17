package com.example.cattletrackingapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cattletrackingapp.R
import com.example.cattletrackingapp.ui.navigation.Screen

// simple controller to toggle the chat overlay from the bottom bar
object ChatOverlayController {
    var open by mutableStateOf(false)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.SearchScreen,
        Screen.Home,
        Screen.SearchByRFID
    )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.SearchScreen -> Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(40.dp)
                        )
                        Screen.Home -> Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(40.dp)
                        )
                        Screen.SearchByRFID -> Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = "Search by RFID",
                            modifier = Modifier.size(40.dp)
                        )
                        else -> Icon(Icons.Filled.Info, contentDescription = null)
                    }
                },
                label = {
                    Text(
                        text = screen.route.replace("_", " ").replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp
                    )
                },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        // Chat button that opens a full-screen chat overlay
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat",
                    modifier = Modifier.size(40.dp)
                )
            },
            label = { Text("Chat", fontSize = 13.sp) },
            selected = false, // overlay isn't a route
            onClick = { ChatOverlayController.open = true },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.secondary,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}
