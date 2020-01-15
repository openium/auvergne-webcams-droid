package fr.openium.auvergnewebcams.ui.webcamdetail.video

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.snackbar

/**
 * Created by laura on 01/12/2017.
 */
class FragmentWebcamVideo : AbstractFragmentWebcam() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO remove that, use ExoPlayer
        val mediaController = MediaController(activity)
        mediaController.setAnchorView(viewViewWebcam)
        mediaController.setMediaPlayer(viewViewWebcam)
        viewViewWebcam.setMediaController(mediaController)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (webcam.viewsurfHD.isNullOrEmpty()) {
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
                if (PreferencesAW.isWebcamsHighQuality(applicationContext!!) && !webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam?.viewsurfHD, webcam?.mediaViewSurfHD)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam?.viewsurfLD, webcam?.mediaViewSurfLD)
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
        val subject = webcam.title

        val url = webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, String.format("%s \n%s", subject, url))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

        chooser.resolveActivity(requireActivity().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
    }

    override fun showProgress() {
        progressbar_detail.show()
    }

}