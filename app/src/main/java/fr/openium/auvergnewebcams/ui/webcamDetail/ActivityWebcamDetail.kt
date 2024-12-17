package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.KEY_WEBCAM_ID
import fr.openium.auvergnewebcams.KEY_WEBCAM_TYPE
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment
import fr.openium.auvergnewebcams.databinding.ContainerToolbarBinding
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.hideSystemUI
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.ext.showSystemUI
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show

/**
 * Created by Openium on 19/02/2019.
 */
class ActivityWebcamDetail : AbstractActivityFragment<ContainerToolbarBinding>() {

    override val showHomeAsUp: Boolean = true

    private var typeWebcam: String? = null

    override fun provideViewBinding(): ContainerToolbarBinding =
        ContainerToolbarBinding.inflate(layoutInflater)

    override fun getDefaultFragment(): Fragment =
        if (typeWebcam == WebcamType.VIEWSURF.jsonKey || typeWebcam == WebcamType.VIDEO.jsonKey) {
            FragmentWebcamDetailVideo()
        } else FragmentWebcamDetailImage()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        typeWebcam = intent?.getStringExtra(KEY_WEBCAM_TYPE)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            window.showSystemUI()
            binding.toolbarContainer.toolbar.show()
        } else {
            window.hideSystemUI()
            binding.toolbarContainer.toolbar.gone()
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
                putExtra(KEY_WEBCAM_ID, webcam.uid)
                putExtra(KEY_WEBCAM_TYPE, webcam.type)
            }
    }
}