package com.example.cattletrackingapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.ui.components.BottomNavBar
import com.example.cattletrackingapp.ui.screens.AddPages.AddCalf.AddCalfScreen
import com.example.cattletrackingapp.ui.screens.AddPages.AddBull.AddBullScreen
import com.example.cattletrackingapp.ui.screens.AddCattleScreen
import com.example.cattletrackingapp.ui.screens.AddPages.AddCow.AddCowScreen
import com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail.BullDetailScreen
import com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail.CalfDetailScreen
import com.example.cattletrackingapp.ui.screens.HerdList.HerdListScreen
import com.example.cattletrackingapp.ui.screens.cowdetail.CowDetailScreen
import com.example.cattletrackingapp.ui.screens.HomeScreen
import com.example.cattletrackingapp.ui.screens.SearchByNameScreen
import com.example.cattletrackingapp.ui.screens.SearchByRFIDScreen
import com.example.cattletrackingapp.ui.screens.vaccinations.VaccinationsScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.DashBoardScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.WeightListScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.WeightModuleScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->

        // Access MainActivity to get the NFC tag data
        val activity = navController.context as? MainActivity
        //val tagData = activity?.nfcTagData ?: "No tag read yet"

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.SearchByName.route) { SearchByNameScreen(navController) }

            // Fixed NFC screen
            composable(Screen.SearchByRFID.route) {
                SearchByRFIDScreen(navController)
            }

            composable(Screen.AddCattle.route) { AddCowScreen(navController) }
            composable (Screen.Vaccinations.route) { VaccinationsScreen(navController) }
            //Created by Eli Herigon
            //dynamic screen, meaning it is passing through the specific cow so it knows which cow to get details on
            composable(Screen.CowDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Screen.CowDetail.ARG_ID).orEmpty()
                CowDetailScreen(navController, id)
            }
            //Create by Nick Heislen
            //Brings you to the add Calf Page
            composable(Screen.AddCalf.route) { AddCalfScreen(navController) }


            composable(Screen.AddBull.route) { AddBullScreen(navController) }
            composable(Screen.ChooseAddCattle.route) { AddCattleScreen(navController) }

            //Takes you to a calf detail page
            composable(Screen.CalfDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("calfId") ?: ""
                CalfDetailScreen(calfId = id, navController = navController)
            }

            //Takes you to a bull detail page
            composable(Screen.BullDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Screen.BullDetail.ARG_ID).orEmpty()
                BullDetailScreen(navController, id)
            }

            composable(Screen.HerdList.route) { HerdListScreen(navController) }

            // Created for the WeightModule ~Braden
            composable(Screen.WeightModule.route) { WeightModuleScreen(navController) }
            composable(Screen.DashBoard.route) { DashBoardScreen(navController) }
            composable(Screen.WeightList.route) { WeightListScreen(navController) }


        }
    }
}