package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.hideSystemUI
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.ext.showSystemUI
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.toolbar.toolbar


/**
 * Created by Openium on 19/02/2019.
 */
class ActivityWebcamDetail : AbstractActivityFragment() {

    override val showHomeAsUp: Boolean = true

    override fun getDefaultFragment(): Fragment =
        if (typeWebcam == WebcamType.VIEWSURF.jsonKey || typeWebcam == WebcamType.VIDEO.jsonKey) {
            FragmentWebcamDetailVideo()
        } else FragmentWebcamDetailImage()

    private var typeWebcam: String? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        typeWebcam = intent?.getStringExtra(Constants.KEY_WEBCAM_TYPE)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            window.showSystemUI()
            toolbar.show()
        } else {
            window.hideSystemUI()
            toolbar.gone()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    // --- Other method
    // ---------------------------------------------------

    companion object {

        fun getIntent(context: Context, webcam: Webcam): Intent =
            Intent(context, ActivityWebcamDetail::class.java).apply {
                putExtra(Constants.KEY_WEBCAM_ID, webcam.uid)
                putExtra(Constants.KEY_WEBCAM_TYPE, webcam.type)
            }
    }
}