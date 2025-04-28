package fr.openium.auvergnewebcams.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.openium.auvergnewebcams.ext.navigateWithLifecycle
import fr.openium.auvergnewebcams.ext.popBackStackWithLifecycle
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.main.components.SectionsListScreen
import fr.openium.auvergnewebcams.ui.search.components.SearchScreen
import fr.openium.auvergnewebcams.ui.sectionDetail.SectionDetailScreen
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
                goToMain = { navHostController.navigateWithLifecycle(Destination.Main) }
            )
        }

        composable<Destination.Main> {
            SectionsListScreen(
                goToWebcamDetail = {
                },
                goToSectionList = { section ->
                    navHostController.navigateWithLifecycle(
                        Destination.SectionDetails(section.uid)
                    )
                },
                goToSearch = {
                    navHostController.navigateWithLifecycle(
                        Destination.Search
                    )
                },
                goToMap = {
                },
                goToSettings = {
                    navHostController.navigateWithLifecycle(
                        Destination.Settings
                    )
                },
            )
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

        composable<Destination.SectionDetails> {
            SectionDetailScreen(
                sectionId = 1, // fix
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = { },
                onNavigateToMap = { }
            )
        }

        composable<Destination.Search> {
            SearchScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = {
                    navHostController.navigateWithLifecycle(
                        Destination.Main // fix
                    )
                }
            )
        }

        composable<Destination.Map> {
            //MapScreen
        }


    }
}
