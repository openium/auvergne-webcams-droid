package fr.openium.auvergnewebcams.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment
import fr.openium.auvergnewebcams.databinding.ContainerToolbarBinding

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettings : AbstractActivityFragment<ContainerToolbarBinding>() {

    override val layoutId: Int = R.layout.container_toolbar
    override val showHomeAsUp: Boolean = true

    // Fragment to be shown by default
    override fun getDefaultFragment(): Fragment? = FragmentSettings()

    override fun provideViewBinding(): ContainerToolbarBinding =
        ContainerToolbarBinding.inflate(layoutInflater)

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set transition animations for the activity
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}
