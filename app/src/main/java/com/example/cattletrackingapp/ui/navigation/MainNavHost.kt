package com.example.cattletrackingapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cattletrackingapp.ui.components.BottomNavBar
import com.example.cattletrackingapp.ui.screens.AddCow.AddCowScreen
import com.example.cattletrackingapp.ui.screens.CattleList.CattleListScreen
import com.example.cattletrackingapp.ui.screens.cowdetail.CowDetailScreen
import com.example.cattletrackingapp.ui.screens.HomeScreen
import com.example.cattletrackingapp.ui.screens.SearchByNameScreen
import com.example.cattletrackingapp.ui.screens.SearchByRFIDScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.SearchByName.route) { SearchByNameScreen(navController) }
            composable(Screen.SearchByRFID.route) { SearchByRFIDScreen(navController) }
            composable(Screen.AddCattle.route) { AddCowScreen(navController) }
            composable(Screen.CattleList.route) { CattleListScreen(navController) }
            //dynamic screen, meaning it is passing through the specific cow so it knows which cow to get details on
            composable(Screen.CowDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Screen.CowDetail.ARG_ID).orEmpty()
                CowDetailScreen(navController, id)
            }

        }
    }
}