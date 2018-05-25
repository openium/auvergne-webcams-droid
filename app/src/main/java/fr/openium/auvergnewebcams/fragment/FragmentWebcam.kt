package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.widget.ImageView
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.hide
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.fragment_webcam.*
import java.io.File
import java.lang.Exception


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (webcam?.imageHD.isNullOrEmpty()) {
                textViewWebcamLowQualityOnly.show()
            } else {
                textViewWebcamLowQualityOnly.gone()
            }
        } else {
            textViewWebcamLowQualityOnly.gone()
        }
    }

    override fun shareWebCam() {
        val subject = webcam?.title

        if (mBigImage.currentImageFile != null) {
            val image = FileProvider.getUriForFile(context!!, context!!.getPackageName() + ".provider", mBigImage.currentImageFile)

            val intent = Intent(Intent.ACTION_SEND).apply {
                setType("application/image")
                putExtra(Intent.EXTRA_TEXT, subject)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_STREAM, image)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(activity?.getPackageManager()) != null) {
                startActivity(intent)
            }
        }
    }

    override fun initWebCam() {
        super.initWebCam()
        if (isAlive) {
            if (webcam?.imageHD.isNullOrEmpty()) {
                textViewWebcamLowQualityOnly.show()
            } else {
                textViewWebcamLowQualityOnly.gone()
            }

            mBigImage.setFailureImage(ContextCompat.getDrawable(context!!, R.drawable.broken_camera))
            mBigImage.setFailureImageInitScaleType(ImageView.ScaleType.FIT_CENTER)

            mBigImage.setImageLoaderCallback(object : ImageLoader.Callback {
                override fun onSuccess(image: File?) {
                    if (isAlive) {
                        progressbar_detail?.hide()
                        itemMenuRefresh?.isEnabled = true
                    }
                }

                override fun onFail(error: Exception?) {
                    if (isAlive) {
                        onLoadWebcamError()
                        progressbar_detail?.hide()
                        itemMenuRefresh?.isEnabled = true
                    }
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

            val display = activity?.getWindowManager()?.getDefaultDisplay()
            val size = Point()
            display?.getSize(size)

            var scaleType: Int? = null
            var thumbnail: Uri? = null
            var image: Uri? = null

            if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_CROP
                if (PreferencesAW.isWebcamsHighQuality(context!!) && !webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam?.viewsurfHD, webcam?.mediaViewSurfHD)
                    image = Uri.parse(urlWebCam)
                } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                    val urlWebCam = String.format("%s/%s.jpg", webcam?.viewsurfLD, webcam?.mediaViewSurfLD)
                    image = Uri.parse(urlWebCam)
                }
            } else {
                if (PreferencesAW.isWebcamsHighQuality(context!!) && !webcam?.imageHD.isNullOrBlank()) {
                    scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_CROP
                    thumbnail = Uri.parse(webcam?.imageLD)
                    image = Uri.parse(webcam?.imageHD)
                } else if (!webcam?.imageLD.isNullOrBlank()) {
                    image = Uri.parse(webcam?.imageLD)
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