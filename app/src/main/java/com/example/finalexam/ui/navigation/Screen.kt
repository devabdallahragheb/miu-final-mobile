package com.example.finalexam.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object ItemList : Screen("item_list/{category}") {
        fun createRoute(category: String) = "item_list/$category"
    }
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Int) = "item_detail/$itemId"
    }
    object Settings : Screen("settings")
}
