package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.fragment.FragmentSettings

/**
 * Created by laura on 04/12/2017.
 */
class ActivitySettings : AbstractActivityFragment() {

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