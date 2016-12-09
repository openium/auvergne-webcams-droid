package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.fragment.FragmentListCamera

class ActivityMain : AbstractActivityFragment() {

    override fun getDefaultFragment(): Fragment? {
        return FragmentListCamera()
    }
}
