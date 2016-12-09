package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.fragment.FragmentWebcam

/**
 * Created by t.coulange on 09/12/2016.
 */
class ActivityWebcam : AbstractActivityFragment() {
    override fun getDefaultFragment(): Fragment? {
        return FragmentWebcam()
    }
}