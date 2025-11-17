package com.example.cattletrackingapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cattletrackingapp.MainActivity
import com.example.cattletrackingapp.ui.components.BottomNavBar
import com.example.cattletrackingapp.ui.screens.AddPages.AddBull.AddBullScreen
import com.example.cattletrackingapp.ui.screens.AddPages.AddCalf.AddCalfScreen
import com.example.cattletrackingapp.ui.screens.AddPages.AddCattleScreen
import com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail.BullDetailScreen
import com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail.CalfDetailScreen
import com.example.cattletrackingapp.ui.screens.HerdList.HerdListScreen
import com.example.cattletrackingapp.ui.screens.HomeScreen
import com.example.cattletrackingapp.ui.screens.SearchPage.SearchScreen
import com.example.cattletrackingapp.ui.screens.Vaccinations.VaccinationsScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.DashBoardScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.WeightListScreen
import com.example.cattletrackingapp.ui.screens.WeightModule.WeightModuleScreen
import com.example.cattletrackingapp.ui.screens.cowdetail.CowDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    var chatOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->

        // Access MainActivity if you need NFC context
        val activity = navController.context as? MainActivity

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }

            composable(Screen.SearchScreen.route) { SearchScreen(navController) }

            // If Screen.AddCattle is meant to add a generic animal, call AddCattleScreen.
            // If it’s specifically cows, keep AddCowScreen but ensure the route name matches.
            composable(Screen.ChooseAddCattle.route) { AddCattleScreen(navController) }
            // If you ALSO have a Screen.AddCow route, keep this too:
            // composable(Screen.AddCow.route) { AddCowScreen(navController) }

            composable(Screen.Vaccinations.route) { VaccinationsScreen(navController) }

            // Cow detail
            composable(Screen.CowDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Screen.CowDetail.ARG_ID).orEmpty()
                CowDetailScreen(navController, id)
            }

            // Add Calf
            composable(Screen.AddCalf.route) { AddCalfScreen(navController) }

            // Add Bull
            composable(Screen.AddBull.route) { AddBullScreen(navController) }

            // Calf detail
            composable(Screen.CalfDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("calfId") ?: ""
                CalfDetailScreen(calfId = id, navController = navController)
            }

            // Bull detail
            composable(Screen.BullDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Screen.BullDetail.ARG_ID).orEmpty()
                BullDetailScreen(navController, id)
            }

            composable(Screen.HerdList.route) { HerdListScreen(navController) }

            // Weight module
            composable(Screen.WeightModule.route) { WeightModuleScreen(navController) }
            composable(Screen.DashBoard.route) { DashBoardScreen(navController) }
            composable(Screen.WeightList.route) { WeightListScreen(navController) }
        }
    }

    // Chat overlay (leave as-is if it exists)
    if (com.example.cattletrackingapp.ui.components.ChatOverlayController.open) {
        com.example.cattletrackingapp.ui.screens.chat.ChatOverlay(
            onClose = { com.example.cattletrackingapp.ui.components.ChatOverlayController.open = false }
        )
    }
}
