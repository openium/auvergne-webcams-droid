package fr.openium.auvergnewebcams.activity

import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.fragment.FragmentSettings

/**
 * Created by Skyle on 20/02/2019.
 */
class ActivitySettings : AbstractActivityFragment() {

    override val layoutId: Int
        get() = R.layout.container_toolbar

    override val showHomeAsUp: Boolean
        get() = true

    override fun getDefaultFragment(): Fragment? {
        return FragmentSettings()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}