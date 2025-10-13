package com.example.cattletrackingapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SearchByName : Screen("search_by_name")
    object SearchByRFID : Screen("search_by_rfid")
    object AddCattle : Screen("add_cattle")

    object CattleList : Screen("cattle_list")

    object CowDetail : Screen("cow_detail/{cowId}") {
        fun routeWithId(cowId: String) = "cow_detail/$cowId"
        const val ARG_ID = "cowId"
    }
    companion object {

    }
}
