package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.fragment.FragmentCarouselWebcam

class ActivityMain : AbstractActivityFragment() {

    override fun getDefaultFragment(): Fragment? {
        return FragmentCarouselWebcam()
    }
}
