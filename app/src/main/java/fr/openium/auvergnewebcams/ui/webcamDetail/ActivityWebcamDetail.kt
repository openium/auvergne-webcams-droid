package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import fr.openium.auvergnewebcams.KEY_WEBCAM_ID
import fr.openium.auvergnewebcams.KEY_WEBCAM_TYPE
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWTheme


/**
 * Created by Openium on 19/02/2019.
 */
class ActivityWebcamDetail : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_details


    override val showHomeAsUp: Boolean = true

    //
//    override fun getDefaultFragment(): Fragment =
//        if (typeWebcam == WebcamType.VIEWSURF.jsonKey || typeWebcam == WebcamType.VIDEO.jsonKey) {
//            FragmentWebcamDetailVideo()
//        } else FragmentWebcamDetailImage()
//
    private var typeWebcam: String? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        val webcamId = intent?.getLongExtra(KEY_WEBCAM_ID, 0L) ?: 0L
        val typeWebcam = intent?.getStringExtra(KEY_WEBCAM_TYPE)

        super.onCreate(savedInstanceState)

        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                DetailScreen(
                    webcamId = webcamId,
                    typeWebcam = typeWebcam,
                    onNavigateBack = { finish() },
                )

            }
        }
    }


    //
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            window.showSystemUI()
//            toolbar.show()
//        } else {
//            window.hideSystemUI()
//            toolbar.gone()
//        }
//    }
//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
//    }
//
//    // --- Other method
//    // ---------------------------------------------------
//
    companion object {

        fun getIntent(context: Context, webcam: Webcam): Intent =
            Intent(context, ActivityWebcamDetail::class.java).apply {
                putExtra(KEY_WEBCAM_ID, webcam.uid)
                putExtra(KEY_WEBCAM_TYPE, webcam.type)
            }
    }
}