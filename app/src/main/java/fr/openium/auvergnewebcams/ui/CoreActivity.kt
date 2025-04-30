package fr.openium.auvergnewebcams.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import fr.openium.auvergnewebcams.ui.navigation.AWNavGraph
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils

class CoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtils.appIsOpen(this)
        AnalyticsUtils.sendAllUserProperties(this)

        enableEdgeToEdge()

        setContent {
            val navHostController = rememberNavController()
            AWTheme {
                AWNavGraph(navHostController = navHostController)
            }
        }
    }
}