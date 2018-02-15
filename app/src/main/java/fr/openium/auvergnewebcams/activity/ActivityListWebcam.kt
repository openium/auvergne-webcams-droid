package fr.openium.auvergnewebcams.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.fragment.FragmentListWebcam

/**
 * Created by nicolas on 19/12/2017.
 */
class ActivityListWebcam : AbstractActivityFragment() {

    override fun getDefaultFragment(): Fragment? {
        return FragmentListWebcam()
    }

    override val showHomeAsUp: Boolean
        get() = true

    companion object {
        fun getBundle(sectionUid: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(Constants.ARG_SECTION_UID, sectionUid)
            return bundle
        }

    }

}