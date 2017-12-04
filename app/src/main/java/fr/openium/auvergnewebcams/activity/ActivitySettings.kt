package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
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
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

}