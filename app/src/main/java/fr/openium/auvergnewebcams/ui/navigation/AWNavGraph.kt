package fr.openium.auvergnewebcams.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fr.openium.auvergnewebcams.ext.navigateWithLifecycle
import fr.openium.auvergnewebcams.ext.popBackStackWithLifecycle
import fr.openium.auvergnewebcams.ui.about.AboutScreen
import fr.openium.auvergnewebcams.ui.main.SectionsListScreen
import fr.openium.auvergnewebcams.ui.map.MapScreen
import fr.openium.auvergnewebcams.ui.search.SearchScreen
import fr.openium.auvergnewebcams.ui.sectionDetail.SectionDetailScreen
import fr.openium.auvergnewebcams.ui.settings.SettingsScreen
import fr.openium.auvergnewebcams.ui.splash.SplashScreen
import fr.openium.auvergnewebcams.ui.webcamDetail.DetailScreen
import fr.openium.auvergnewebcams.utils.AnalyticsUtils

@Composable
fun AWNavGraph(navHostController: NavHostController) {
    val context = LocalContext.current
    NavHost(
        navController = navHostController,
        startDestination = Destination.Splash,
    ) {

        composable<Destination.Splash> {
            SplashScreen(
                goToMain = { navHostController.navigateWithLifecycle(Destination.Main) }
            )
        }

        composable<Destination.Main> {
            SectionsListScreen(
                goToWebcamDetail = { webcam ->
                    AnalyticsUtils.webcamDetailsClicked(context = context, webcamTitle = webcam.title ?: "")
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
                },
                goToSectionList = { section ->
                    AnalyticsUtils.webcamDetailsClicked(context = context, webcamTitle = section.title ?: "")
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
                    navHostController.navigateWithLifecycle(Destination.Map(null, null))
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
                sectionId = it.savedStateHandle.toRoute(Destination.SectionDetails::class).sectionId,
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = { webcam ->
                    AnalyticsUtils.webcamDetailsClicked(context = context, webcamTitle = webcam.title ?: "")
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
                },
                onNavigateToMap = { sectionId, sectionTitle ->
                    navHostController.navigateWithLifecycle(Destination.Map(sectionId, sectionTitle))
                }
            )
        }

        composable<Destination.Search> {
            SearchScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
                goToWebcamDetail = { webcam ->
                    AnalyticsUtils.webcamDetailsClicked(context = context, webcamTitle = webcam.title ?: "")
                    navHostController.navigateWithLifecycle(
                        Destination.WebcamDetails(webcam.uid)
                    )
                },
            )
        }

        composable<Destination.Map> {
            MapScreen(onNavigateBack = navHostController::popBackStackWithLifecycle,
                goToWebcamDetail = { webcam ->
                    AnalyticsUtils.webcamDetailsClicked(
                        context = context,
                        webcamTitle = webcam.title ?: "",
                    )
                    navHostController.navigateWithLifecycle(Destination.WebcamDetails(webcamId = webcam.uid))
                })
        }



        composable<Destination.WebcamDetails> {
            DetailScreen(
                onNavigateBack = { navHostController.popBackStackWithLifecycle() },
            )
        }

    }
}
