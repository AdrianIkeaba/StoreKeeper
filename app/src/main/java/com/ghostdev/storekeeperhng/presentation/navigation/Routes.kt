package com.ghostdev.storekeeperhng.presentation.navigation

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Home : Route("home")
    object AddProduct : Route("add")
    object EditProduct : Route("edit/{productId}") {
        fun create(productId: Long) = "edit/$productId"
    }
    object Detail : Route("detail/{productId}") {
        fun create(productId: Long) = "detail/$productId"
    }
    object Camera : Route("camera")
}