package fr.openium.auvergnewebcams.ui.about

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.gone
import kotlinx.android.synthetic.main.activity_about.*

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettingsAbout : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_about

    override val showHomeAsUp: Boolean = true

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        composeView.setContent {
            AWTheme {
                AboutScreen(
                    navigateBack = {
                        finish()
                    }
                )
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}