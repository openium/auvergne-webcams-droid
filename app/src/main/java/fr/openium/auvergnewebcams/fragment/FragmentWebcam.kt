package fr.openium.auvergnewebcams.fragment

import android.graphics.Point
import android.net.Uri
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam


    override fun initWebCam() {
        if (isAlive) {
            val display = activity!!.getWindowManager().getDefaultDisplay()
            val size = Point()
            display.getSize(size)

            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
                    mBigImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_AUTO)
                    mBigImage.showImage(Uri.parse(urlWebCam))
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
                    mBigImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_AUTO)
                    mBigImage.showImage(Uri.parse(urlWebCam))
                }
            } else {
                if (!webcam!!.imageHD.isNullOrBlank()) {
                    mBigImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_AUTO)
                    mBigImage.showImage(Uri.parse(webcam!!.imageLD!!), Uri.parse(webcam!!.imageHD!!))
                } else if (!webcam!!.imageLD.isNullOrBlank()) {
                    mBigImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE)
                    mBigImage.showImage(Uri.parse(webcam!!.imageLD!!))
                }
            }

        }
    }

}