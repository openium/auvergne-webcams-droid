package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.footer_webcam_detail.*
import kotlinx.android.synthetic.main.fragment_webcam_image.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.min


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentWebcamDetailImage : AbstractFragmentWebcam() {

    override val layoutId: Int = R.layout.fragment_webcam_image

    private var zoomTimer: Disposable? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initBigImageViewListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE && webcam.imageHD.isNullOrEmpty()) {
            textViewWebcamLowQualityOnly.show()
        } else {
            textViewWebcamLowQualityOnly.gone()
        }
    }

    // --- Methods
    // ---------------------------------------------------

    private fun initBigImageViewListener() {
        bigImageViewWebcamImage.setImageLoaderCallback(object : ImageLoader.Callback {
            override fun onSuccess(image: File?) {
                progressBarWebcamImageDetail?.gone()

                // Start Timer cause BigImageView SSIV is not implementing method onReady...
                setZoomTimer()
            }

            override fun onFail(error: Exception?) {
                updateDisplay(State.NOT_WORKING)
                progressBarWebcamImageDetail?.gone()
                Timber.e(error, "Error loading big images")
            }

            override fun onStart() {
                progressBarWebcamImageDetail?.show()
            }

            override fun onFinish() {}
            override fun onCacheHit(imageType: Int, image: File?) {}
            override fun onCacheMiss(imageType: Int, image: File?) {}
            override fun onProgress(progress: Int) {}
        })
    }

    private fun setZoomTimer() {
        zoomTimer?.dispose()
        zoomTimer = null

        zoomTimer = Observable.timer(25, TimeUnit.MILLISECONDS).subscribe({
            if (bigImageViewWebcamImage.ssiv.isReady) {
                bigImageViewWebcamImage.ssiv.animateScaleAndCenter(
                    min(bigImageViewWebcamImage.ssiv.maxScale, getZoomValue()),
                    bigImageViewWebcamImage.ssiv.center
                )?.withInterruptible(false)?.withDuration(300L)?.start()
            } else {
                setZoomTimer()
            }
        }, { Timber.e(it, "Error refresh timer") }).addTo(disposables)
    }

    // If you want to understand this a little bit more, look inside com/github/piasy/biv/utils/DisplayOptimizeListener.class onReady method
    private fun getZoomValue(): Float {
        var zoomValue = if (bigImageViewWebcamImage.ssiv.sWidth <= bigImageViewWebcamImage.ssiv.sHeight) {
            bigImageViewWebcamImage.ssiv.width.toFloat() / bigImageViewWebcamImage.ssiv.sWidth
        } else {
            bigImageViewWebcamImage.ssiv.height.toFloat() / bigImageViewWebcamImage.ssiv.sHeight
        }

        if (abs(zoomValue - 0.1) < 0.2f) {
            zoomValue += 0.2f
        }

        return zoomValue * 0.75f
    }

    override fun setWebcam() {
        var imageURL: Uri? = null

        if (prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()) {
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false))
        } else if (!webcam.imageLD.isNullOrBlank()) {
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
        }

        bigImageViewWebcamImage.apply {
            setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE)
            imageURL?.also { showImage(imageURL) }
        }
    }

    override fun resetWebcam() {
        updateDisplay()
        setWebcam()
    }

    override fun shareWebCam() {
        bigImageViewWebcamImage.currentImageFile?.also {
            val image = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                bigImageViewWebcamImage.currentImageFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/image"
                putExtra(Intent.EXTRA_TEXT, webcam.title)
                putExtra(Intent.EXTRA_SUBJECT, webcam.title)
                putExtra(Intent.EXTRA_STREAM, image)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

            chooser.resolveActivity(requireActivity().packageManager)?.also {
                startActivity(chooser)
            } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

    override fun saveWebcam() {
        val urlSrc = webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)
        val fileName = String.format("%s_%s.jpg", webcam.title ?: "", System.currentTimeMillis().toString())

        startService(urlSrc, true, fileName)
    }

    override fun refreshWebcam() {
        if (requireContext().hasNetwork) {
            resetWebcam()
        } else {
            snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
        }
    }
}