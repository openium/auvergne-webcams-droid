package fr.openium.auvergnewebcams.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettings : AbstractActivityFragment() {

    override val layoutId: Int = R.layout.container_toolbar

    override val showHomeAsUp: Boolean = true

    override fun getDefaultFragment(): Fragment? = FragmentSettings()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}