package com.example.cattletrackingapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SearchScreen : Screen("search_screen")
    object SearchByRFID : Screen("search_by_rfid")
    object AddCattle : Screen("add_cattle")
    object Vaccinations : Screen("vaccinations")

    object CowDetail : Screen("cow_detail/{cowId}") {
        fun routeWithId(cowId: String?) = "cow_detail/$cowId"
        const val ARG_ID = "cowId"
    }
    object AddCalf : Screen("add_calf")


    //Nick Heislen 10/14/2023
    object CalfDetail : Screen("calf_detail/{calfId}") {
        fun routeWithId(id: String?) = "calf_detail/$id"
    }

    //Nick Heislen 10/22/2025
    object BullDetail : Screen("bull_detail/{bullId}") {
        fun routeWithId(bullId: String?) = "bull_detail/$bullId"
        const val ARG_ID = "bullId"
    }

    object AddBull : Screen("add_bull")
    object ChooseAddCattle : Screen("choose_add_cattle")
    companion object {

    }

    object HerdList : Screen("herd_list")

    object WeightModule : Screen("weight_module")
    object DashBoard : Screen("dashboard")
    object WeightList : Screen("weight_list")

}
