package fr.openium.auvergnewebcams.fragment

import android.graphics.Point
import android.net.Uri
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.hide
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam.*
import java.io.File
import java.lang.Exception


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam


    override fun initWebCam() {
        if (isAlive) {
            mBigImage.setImageLoaderCallback(object : ImageLoader.Callback {
                override fun onSuccess(image: File?) {
                    progressbar_detail?.hide()
                }

                override fun onFail(error: Exception?) {
                }

                override fun onCacheHit(image: File?) {
                }

                override fun onCacheMiss(image: File?) {
                }

                override fun onProgress(progress: Int) {
                }

                override fun onStart() {
                }

                override fun onFinish() {
                }

            })

            val display = activity!!.getWindowManager().getDefaultDisplay()
            val size = Point()
            display.getSize(size)


            var scaleType: Int? = null
            var thumbnail: Uri? = null
            var image: Uri? = null


            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                scaleType = BigImageView.INIT_SCALE_TYPE_AUTO
                if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
                    image = Uri.parse(urlWebCam)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
                    image = Uri.parse(urlWebCam)
                }
            } else {
                if (!webcam!!.imageHD.isNullOrBlank()) {
                    scaleType = BigImageView.INIT_SCALE_TYPE_AUTO
                    thumbnail = Uri.parse(webcam!!.imageLD!!)
                    image = Uri.parse(webcam!!.imageHD!!)
                } else if (!webcam!!.imageLD.isNullOrBlank()) {
                    image = Uri.parse(webcam!!.imageLD!!)
                    scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE
                }
            }



            if (scaleType != null) {
                mBigImage.setInitScaleType(scaleType)
            }
            if (image != null) {
                if (thumbnail != null) {
                    mBigImage.showImage(thumbnail, image)
                } else {
                    mBigImage.showImage(image)
                }
            }
        }
    }

    override fun showProgress() {
        progressbar_detail.show()
    }

}