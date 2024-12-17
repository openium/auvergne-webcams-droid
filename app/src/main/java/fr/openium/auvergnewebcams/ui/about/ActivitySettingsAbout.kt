package fr.openium.auvergnewebcams.ui.about

import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.databinding.ComposeViewBinding
import fr.openium.auvergnewebcams.ui.about.components.AboutScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettingsAbout : AbstractActivity<ComposeViewBinding>() {

    override val layoutId: Int = R.layout.compose_view

    override val showHomeAsUp: Boolean = true

    override fun provideViewBinding(): ComposeViewBinding =
        ComposeViewBinding.inflate(layoutInflater)

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        binding.composeView.setContent {
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
