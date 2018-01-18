package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.hide
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import kotlinx.android.synthetic.main.fragment_webcam_video.*

/**
 * Created by laura on 01/12/2017.
 */
class FragmentWebcamVideo : AbstractFragmentWebcam() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaController = MediaController(applicationContext)
        mediaController.setAnchorView(viewViewWebcam)
        mediaController.setMediaPlayer(viewViewWebcam)
        viewViewWebcam.setMediaController(mediaController)
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (webcam?.viewsurfHD.isNullOrEmpty()) {
                textViewWebcamLowQualityOnly.show()
            } else {
                textViewWebcamLowQualityOnly.gone()
            }
        } else {
            textViewWebcamLowQualityOnly.gone()
        }
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_webcam_video

    override fun initWebCam() {
        super.initWebCam()
        if (isAlive) {
            if (webcam?.viewsurfHD.isNullOrEmpty()) {
                textViewWebcamLowQualityOnly.show()
            } else {
                textViewWebcamLowQualityOnly.gone()
            }
            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                var urlWebcam = ""
                if (PreferencesAW.isWebcamsHighQuality(applicationContext) && !webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
                }

                val videoUri = Uri.parse(urlWebcam)
                viewViewWebcam.setVideoURI(videoUri)

                viewViewWebcam.setOnPreparedListener {
                    progressbar_detail?.hide()
                    imageViewErrorLoad?.hide()
                    itemMenuRefresh?.isEnabled = true
                }
                viewViewWebcam.setOnErrorListener { _, _, _ ->
                    progressbar_detail?.hide()
                    imageViewErrorLoad?.show()
                    onLoadWebcamError()
                    itemMenuRefresh?.isEnabled = true
                    true
                }
                viewViewWebcam.start()

            }
        }
    }

    override fun shareWebCam() {
        val subject = webcam?.title

        val url = webcam?.getUrlForWebcam(true, true) ?: ""
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_TEXT, String.format("%s \n%s", subject, url))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(activity?.getPackageManager()) != null) {
            startActivity(intent)
        }

    }

    override fun showProgress() {
        progressbar_detail.show()
    }

}