package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.startActivity
import kotlinx.android.synthetic.main.fragment_search.composeView

class ActivitySplash : AbstractActivity() {
    
    override val layoutId: Int = R.layout.fragment_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeView.setContent {
            AWTheme {
                SplashScreen(startActivityMain = { startActivityMain() })
            }
        }
    }

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        finish()
    }

}