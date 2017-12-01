package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.fragment.FragmentWebcam
import fr.openium.auvergnewebcams.fragment.FragmentWebcamVideo
import fr.openium.auvergnewebcams.model.Webcam

/**
 * Created by t.coulange on 09/12/2016.
 */
class ActivityWebcam : AbstractActivityFragment() {

    override fun getDefaultFragment(): Fragment? {
        val type = intent?.getStringExtra(Constants.KEY_TYPE)
        if (type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            return FragmentWebcamVideo()
        } else {
            return FragmentWebcam()
        }
    }
}