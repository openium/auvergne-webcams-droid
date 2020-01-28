package fr.openium.auvergnewebcams.ui.webcamdetail

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import kotlinx.android.synthetic.main.footer_webcam_detail.*
import kotlinx.android.synthetic.main.fragment_webcam_image.*
import java.io.File


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentWebcamImage : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam_image

    // --- Life cycle
    // ---------------------------------------------------

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bigImageViewWebcamImage.setFailureImage(ContextCompat.getDrawable(requireContext(), R.drawable.broken_camera))
        bigImageViewWebcamImage.setFailureImageInitScaleType(ImageView.ScaleType.CENTER)
        bigImageViewWebcamImage.setImageLoaderCallback(object : ImageLoader.Callback {
            override fun onSuccess(image: File?) {
                // TODO remove and use built-in option
                progressBarWebcamImageDetail?.gone()
            }

            override fun onFail(error: Exception?) {
                updateDisplay(State.NOT_WORKING)
                progressBarWebcamImageDetail?.gone()
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

    override fun setWebcam() {
        var scaleType: Int? = null
        var imageURL: Uri? = null

        if (prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()) {
            scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_CROP
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false))
        } else if (!webcam.imageLD.isNullOrBlank()) {
            scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE
            imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
        }

        bigImageViewWebcamImage.apply {
            scaleType?.also { setInitScaleType(it) }
            imageURL?.also { showImage(imageURL) }
        }
    }

    override fun refreshWebcam() {
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
}