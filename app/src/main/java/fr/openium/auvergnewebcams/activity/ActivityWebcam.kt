package fr.openium.auvergnewebcams.activity

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.fragment.FragmentWebcam
import fr.openium.auvergnewebcams.fragment.FragmentWebcamVideo
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by t.coulange on 09/12/2016.
 */
class ActivityWebcam : AbstractActivityFragment() {

    private var typeWebcam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        typeWebcam = intent?.getStringExtra(Constants.KEY_TYPE)
        super.onCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar.show()
        } else {
            toolbar.gone()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    override fun getDefaultFragment(): Fragment? {
        if (typeWebcam == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            return FragmentWebcamVideo()
        } else {
            return FragmentWebcam()
        }
    }

    override val showHomeAsUp: Boolean
        get() = true
}