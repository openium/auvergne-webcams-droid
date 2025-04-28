package fr.openium.auvergnewebcams.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.openium.afu.ext.navigateWithLifecycle
import fr.openium.afu.ext.popBackStackWithLifecycle
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.settings.SettingsScreen
import fr.openium.auvergnewebcams.ui.splash.SplashScreen

@Composable
fun AWNavGraph(navHostController: NavHostController) {

    NavHost(
        navController = navHostController,
        startDestination = Destination.Splash,
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
    ) {

        composable<Destination.Splash> {
            SplashScreen(
                goToMain = { navHostController.navigateWithLifecycle(Destination.Settings) }
            )
        }

        composable<Destination.Main> {

        }

        composable<Destination.Settings> {
            SettingsScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                navigateTo = { destination ->
                    navHostController.navigateWithLifecycle(
                        destination
                    )
                })
        }

        composable<Destination.About> {
            AboutScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() }
            )
        }

    }
}
