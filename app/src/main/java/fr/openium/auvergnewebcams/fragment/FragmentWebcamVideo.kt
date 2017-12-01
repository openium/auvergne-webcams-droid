package fr.openium.auvergnewebcams.fragment

import android.net.Uri
import android.widget.MediaController
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam_video.*

/**
 * Created by laura on 01/12/2017.
 */
class FragmentWebcamVideo : AbstractFragmentWebcam() {

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_webcam_video

    override fun initWebCam() {
        if (isAlive) {
            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                var urlWebcam = ""
                if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    urlWebcam = String.format("%s/%s.mp4", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
                }
                val mediaController = MediaController(activity)
                mediaController.setAnchorView(viewViewWebcam)
                mediaController.setMediaPlayer(viewViewWebcam)

                val videoUri = Uri.parse(urlWebcam)
                viewViewWebcam.setMediaController(mediaController)
                viewViewWebcam.setVideoURI(videoUri)
                viewViewWebcam.start()

            }
        }
    }


}