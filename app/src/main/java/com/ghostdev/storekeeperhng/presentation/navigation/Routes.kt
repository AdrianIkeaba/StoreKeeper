package com.ghostdev.storekeeperhng.presentation.navigation

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Onboarding : Route("onboarding")
    object Main : Route("main")
    object Home : Route("home")
    object Profile : Route("profile")
    object AddProduct : Route("add")
    object EditProduct : Route("edit/{productId}") {
        fun create(productId: Long) = "edit/$productId"
    }
    object Detail : Route("detail/{productId}") {
        fun create(productId: Long) = "detail/$productId"
    }
}