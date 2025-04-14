package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.startActivity

class ActivitySplash : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                SplashScreen(goToMain = { startActivityMain() })
            }
        }
    }

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        finish()
    }

}