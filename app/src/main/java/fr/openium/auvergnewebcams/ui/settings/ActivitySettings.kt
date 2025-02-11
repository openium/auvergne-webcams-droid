package fr.openium.auvergnewebcams.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import kotlinx.android.synthetic.main.fragment_search.composeView

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettings : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_settings

    override val showHomeAsUp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        composeView.setContent {
            AWTheme {
                SettingsScreen(
                    navigateToActivity = { activityClass ->
                        val intent = Intent(this, activityClass)
                        startActivity(intent)
                    },
                    navigateToUrl = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    })
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }


}