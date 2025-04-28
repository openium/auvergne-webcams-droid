package fr.openium.auvergnewebcams.ui.settings

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.theme.AWTheme

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettings : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                SettingsScreen(
                    onNavigateBack = { finish() },
                    navigateTo = { destination ->
                    },
                )
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }


}