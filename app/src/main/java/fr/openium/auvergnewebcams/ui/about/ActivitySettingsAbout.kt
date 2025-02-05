package fr.openium.auvergnewebcams.ui.about

import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import kotlinx.android.synthetic.main.fragment_main.composeView

class ActivitySettingsAbout : AbstractActivity() {

    override val showHomeAsUp: Boolean = true

    override val layoutId: Int = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        composeView.setContent {
            AWTheme {
                AboutScreen()
            }
        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }


}
