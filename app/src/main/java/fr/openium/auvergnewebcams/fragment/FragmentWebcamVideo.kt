package fr.openium.auvergnewebcams.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.hide
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam_video.*

/**
 * Created by laura on 01/12/2017.
 */
class FragmentWebcamVideo : AbstractFragmentWebcam() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaController = MediaController(activity)
        mediaController.setAnchorView(viewViewWebcam)
        mediaController.setMediaPlayer(viewViewWebcam)
        viewViewWebcam.setMediaController(mediaController)
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_webcam_video

    override fun initWebCam() {
        super.initWebCam()
        if (isAlive) {
            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                var urlWebcam = ""
                if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
                }

                val videoUri = Uri.parse(urlWebcam)
                viewViewWebcam.setVideoURI(videoUri)
                viewViewWebcam.setOnPreparedListener {
                    progressbar_detail?.hide()
                }
                viewViewWebcam.start()

            }
        }
    }

    override fun showProgress() {
        progressbar_detail.show()
    }

}