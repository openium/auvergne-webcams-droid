package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import com.github.piasy.biv.view.BigImageView
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.custom.SimpleImageLoaderCallback
import fr.openium.auvergnewebcams.event.eventHasNetwork
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.goneWithAnimationCompat
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.showWithAnimationCompat
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.footer_webcam_detail.textViewWebcamDetailLowQualityOnly
import kotlinx.android.synthetic.main.fragment_webcam_image.bigImageViewWebcamImageDetail
import kotlinx.android.synthetic.main.fragment_webcam_image.linearLayoutWebcamImageDetailContent
import kotlinx.android.synthetic.main.fragment_webcam_image.progressBarWebcamImageDetail
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBigImageViewListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE && webcam.imageHD.isNullOrEmpty()) {
            textViewWebcamDetailLowQualityOnly.show()
        } else textViewWebcamDetailLowQualityOnly.gone()
    }

    private fun initBigImageViewListener() {
        bigImageViewWebcamImageDetail.setImageLoaderCallback(object : SimpleImageLoaderCallback() {
            override fun onSuccess(image: File) {
                wasLastTimeLoadingSuccessful = true
                progressBarWebcamImageDetail.gone()

                // Start Timer cause BigImageView SSIV is not implementing method onReady...
                setZoomTimer()
            }

            override fun onFail(error: Exception) {
                wasLastTimeLoadingSuccessful = false
                updateDisplay()
                progressBarWebcamImageDetail.gone()
                Timber.e(error)
            }

            override fun onStart() {
                progressBarWebcamImageDetail.show()
            }
        })
    }

    private fun setZoomTimer() {
        zoomTimer?.dispose()
        zoomTimer = null

        zoomTimer = Observable.timer(25, TimeUnit.MILLISECONDS).fromIOToMain().subscribe({
            if (bigImageViewWebcamImageDetail.ssiv.isReady) {
                bigImageViewWebcamImageDetail.ssiv.animateScaleAndCenter(
                    min(bigImageViewWebcamImageDetail.ssiv.maxScale, getZoomValue()),
                    bigImageViewWebcamImageDetail.ssiv.center
                )?.withInterruptible(false)?.withDuration(200L)?.start()
            } else {
                setZoomTimer()
            }
        }, { Timber.e(it, "Error refresh timer") }).addTo(disposables)
    }

    // If you want to understand this a little bit more, look inside com/github/piasy/biv/utils/DisplayOptimizeListener.class onReady method
    private fun getZoomValue(): Float {
        var zoomValue = if (bigImageViewWebcamImageDetail.ssiv.sWidth <= bigImageViewWebcamImageDetail.ssiv.sHeight) {
            bigImageViewWebcamImageDetail.ssiv.width.toFloat() / bigImageViewWebcamImageDetail.ssiv.sWidth
        } else {
            bigImageViewWebcamImageDetail.ssiv.height.toFloat() / bigImageViewWebcamImageDetail.ssiv.sHeight
        }

        if (abs(zoomValue - 0.1) < 0.2f) {
            zoomValue += 0.2f
        }

        return zoomValue * 0.75f
    }

    // --- Override Methods
    // ---------------------------------------------------

    override fun showDetailContent() {
        linearLayoutWebcamImageDetailContent.showWithAnimationCompat()
    }

    override fun hideDetailContent() {
        linearLayoutWebcamImageDetailContent.goneWithAnimationCompat()
    }

    override fun setWebcam() {
        var imageURL: Uri? = null

        if (prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()) {
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false))
        } else if (!webcam.imageLD.isNullOrBlank()) {
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
        }
        Timber.d("IMAGE URL ${imageURL?.toString()}")

        bigImageViewWebcamImageDetail.apply {
            setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE)
            imageURL?.also { showImage(imageURL) }
        }
    }

    override fun resetWebcam() {
        updateDisplay()
        setWebcam()
    }

    override fun shareWebCam() {
        bigImageViewWebcamImageDetail.currentImageFile?.also {
            val image = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                it
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
        if (eventHasNetwork.value == true) {
            resetWebcam()
        } else snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
    }
}