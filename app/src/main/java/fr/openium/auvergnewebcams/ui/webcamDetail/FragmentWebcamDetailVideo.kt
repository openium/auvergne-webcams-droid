package fr.openium.auvergnewebcams.ui.webcamDetail

import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam

/**
 * Created by Openium on 19/02/2019.
 */
class FragmentWebcamDetailVideo : AbstractFragmentWebcam() {

    override fun isVideo(): Boolean {
        return true
    }
}