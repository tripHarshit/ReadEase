package com.example.readease.navigation

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.readease.screens.home.HomeScreen
import com.example.readease.screens.search.SearchScreen
import com.example.readease.screens.login.LoginScreen
import com.example.readease.screens.login.SignUpScreen
import com.example.readease.screens.SplashScreen
import com.example.readease.screens.details.BookDetailsScreen
import com.example.readease.screens.details.BookDetailsViewModel
import com.example.readease.screens.search.HomeScreenViewModel
import com.example.readease.screens.search.SearchViewModel
import com.example.readease.screens.stats.StatsScreen
import com.example.readease.screens.updates.UpdatesScreen

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun ReadEaseNavigation() {
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)
    val homeViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.factory)
    val bookDetailsViewModel: BookDetailsViewModel = viewModel(factory = BookDetailsViewModel.factory)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ReadEaseScreens.SplashScreen.name
    ) {
        composable(
            route = ReadEaseScreens.SplashScreen.name,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
        ) {
            SplashScreen(navController = navController)
        }
        composable(
            route = ReadEaseScreens.LoginScreen.name,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
        ) {
            LoginScreen(navController = navController)
        }
        composable(
            route = ReadEaseScreens.SignUpScreen.name,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
        ) {
            SignUpScreen(navController = navController)
        }
        composable(
            route = ReadEaseScreens.ReaderHomeScreen.name,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(600)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(600)) }
        ) {
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }
        composable(
            route = "${ReadEaseScreens.DetailsScreen.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(navController = navController, bookId = bookId, bookDetailsViewModel)
        }
        composable(
            route = "${ReadEaseScreens.UpdateScreen.name}/{bookId}",
            arguments = listOf(navArgument(name = "bookId") { type = NavType.StringType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(600)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(600)) }
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            UpdatesScreen(navController, bookId = bookId, homeViewModel, bookDetailsViewModel)
        }
        composable(
            route = ReadEaseScreens.ReaderStatsScreen.name,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            StatsScreen(navController, homeViewModel)
        }
        composable(
            route = ReadEaseScreens.SearchScreen.name,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
        ) {
            SearchScreen(navController = navController, viewModel)
        }
    }
}
