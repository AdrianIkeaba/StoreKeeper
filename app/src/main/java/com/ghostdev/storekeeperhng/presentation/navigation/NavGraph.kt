package com.ghostdev.storekeeperhng.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ghostdev.storekeeperhng.presentation.screens.AddEditScreen
import com.ghostdev.storekeeperhng.presentation.screens.DetailScreen
import com.ghostdev.storekeeperhng.presentation.screens.HomeScreen
import com.ghostdev.storekeeperhng.presentation.screens.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Route.Splash.route, modifier = modifier) {
        composable(Route.Splash.route) {
            val prefs = org.koin.androidx.compose.get<com.ghostdev.storekeeperhng.data.prefs.ProfilePrefs>()
            val firstRun by prefs.firstRun.collectAsState(initial = true)
            SplashScreen(
                onFinished = {
                    if (firstRun) {
                        navController.navigate(Route.Onboarding.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Route.Onboarding.route) {
            com.ghostdev.storekeeperhng.presentation.screens.OnboardingScreen(
                onCompleted = {
                    navController.navigate(Route.Main.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Main.route) {
            com.ghostdev.storekeeperhng.presentation.screens.MainScreen(
                onAddClick = { navController.navigate(Route.AddProduct.route) },
                onItemClick = { id -> navController.navigate(Route.Detail.create(id)) },
                onEditClick = { id -> navController.navigate(Route.EditProduct.create(id)) },
                onResetToSplash = {
                    navController.navigate(Route.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.AddProduct.route) {
            AddEditScreen(
                productId = null,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Route.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productId") ?: 0L
            AddEditScreen(productId = id, onBack = { navController.popBackStack() })
        }
        composable(
            route = Route.Detail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productId") ?: 0L
            DetailScreen(productId = id, onBack = { navController.popBackStack() }, onEdit = { pid -> navController.navigate(Route.EditProduct.create(pid)) })
        }
    }
}