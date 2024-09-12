package fr.openium.auvergnewebcams.singleactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import fr.openium.auvergnewebcams.singleactivity.navigation.AWGraph
import fr.openium.auvergnewebcams.singleactivity.theme.TempAWTheme
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@OptIn(ExperimentalMaterial3Api::class)
class AWActivity : ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            setContent {
                val navHostController = rememberNavController()

                TempAWTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            AWGraph(navHostController)
                        }
                    }
                }
            }

        }
    }
}
