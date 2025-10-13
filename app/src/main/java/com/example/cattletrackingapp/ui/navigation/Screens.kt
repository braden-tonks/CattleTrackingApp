package com.example.cattletrackingapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SearchByName : Screen("search_by_name")
    object SearchByRFID : Screen("search_by_rfid")
    object AddCattle : Screen("add_cattle")
    object CattleList : Screen("cattle_list")
    object AddCalf : Screen("add_calf")
    object AddBull : Screen("add_bull")
    object ChooseAddCattle : Screen("choose_add_cattle")
    companion object {

    }
}
