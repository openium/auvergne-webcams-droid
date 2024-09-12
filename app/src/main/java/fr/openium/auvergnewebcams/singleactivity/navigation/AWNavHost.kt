package fr.openium.auvergnewebcams.singleactivity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen


@Composable
fun AWGraph(navHostController: NavHostController) {

    /**
     * add destinations to file Destinations.kt
     * https://developer.android.com/guide/navigation/design/type-safety
     */

    NavHost(
        navController = navHostController,
        startDestination = Destination.Splash
    ) {
        composable<Destination.Splash> {
            SplashScreen()
        }

        composable<Destination.Main> {
            SplashScreen()
        }
    }
}
