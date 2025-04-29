package fr.openium.auvergnewebcams.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fr.openium.auvergnewebcams.ext.navigateWithLifecycle
import fr.openium.auvergnewebcams.ext.popBackStackWithLifecycle
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.main.components.SectionsListScreen
import fr.openium.auvergnewebcams.ui.search.components.SearchScreen
import fr.openium.auvergnewebcams.ui.sectionDetail.SectionDetailScreen
import fr.openium.auvergnewebcams.ui.settings.SettingsScreen
import fr.openium.auvergnewebcams.ui.splash.SplashScreen
import fr.openium.auvergnewebcams.ui.webcamDetail.DetailScreen

@Composable
fun AWNavGraph(navHostController: NavHostController) {

    NavHost(
        navController = navHostController,
        startDestination = Destination.Splash,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500)
            )
        },
    ) {

        composable<Destination.Splash> {
            SplashScreen(
                goToMain = { navHostController.navigateWithLifecycle(Destination.Main) }
            )
        }

        composable<Destination.Main> {
            SectionsListScreen(
                goToWebcamDetail = { webcam ->
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
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
                    //TODO
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
                sectionId = it.savedStateHandle.toRoute(Destination.SectionDetails::class).sectionID,
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = { webcam ->
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
                },
                onNavigateToMap = {
                    //TODO
                }
            )
        }

        composable<Destination.Search> {
            SearchScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = { webcam ->
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
                },
            )
        }

        composable<Destination.Map> {
            //MapScreen
            //TODO
        }

        composable<Destination.MapSection> {
            //MapScreen
            //TODO
        }


        composable<Destination.WebcamDetails> {
            DetailScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
            )
        }

    }
}
