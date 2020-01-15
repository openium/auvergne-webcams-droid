package fr.openium.auvergnewebcams.ui.webcamdetail.image

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import kotlinx.android.synthetic.main.fragment_webcam_image.*
import java.io.File


/**
 * Created by Openium on 09/12/2016.
 */
class FragmentWebcamImage : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam_image

    // --- Life cycle
    // ---------------------------------------------------

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        updateDisplay()

//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            frameLayoutWebcamDetailHeader.show()
//
//            if (webcam.imageHD.isNullOrEmpty()) {
//                textViewWebcamImageLowQualityOnly.show()
//            } else {
//                textViewWebcamImageLowQualityOnly.gone()
//            }
//
//            if (webcam.isUpToDate()) {
//                textViewWebcamDetailErrorMessage.gone()
//            } else {
//                textViewWebcamDetailErrorMessage.text = getString(R.string.generic_not_up_to_date)
//                textViewWebcamDetailErrorMessage.show()
//            }
//        } else {
//            textViewWebcamImageLowQualityOnly.gone()
//            frameLayoutWebcamDetailHeader.gone()
//            textViewWebcamDetailErrorMessage.gone()
//        }
    }

    // --- Methods
    // ---------------------------------------------------

    override fun updateDisplay(forcedState: State?) {
        super.updateDisplay(forcedState)

        if (webcam.imageHD.isNullOrEmpty()) {
            textViewWebcamImageLowQualityOnly.show()
        } else {
            textViewWebcamImageLowQualityOnly.gone()
        }

        bigImageViewWebcamImage.setFailureImage(ContextCompat.getDrawable(requireContext(), R.drawable.broken_camera))
        bigImageViewWebcamImage.setFailureImageInitScaleType(ImageView.ScaleType.FIT_CENTER)

        bigImageViewWebcamImage.setImageLoaderCallback(object : ImageLoader.Callback {
            override fun onSuccess(image: File?) {
                progressBarWebcamImageDetail?.gone()
                itemMenuRefresh?.isEnabled = true
            }

            override fun onFail(error: Exception?) {
                updateDisplay(State.NOT_WORKING)
                progressBarWebcamImageDetail?.gone()
                itemMenuRefresh?.isEnabled = true
            }

            override fun onCacheHit(imageType: Int, image: File?) {}
            override fun onCacheMiss(imageType: Int, image: File?) {}
            override fun onProgress(progress: Int) {}
            override fun onStart() {}
            override fun onFinish() {}
        })

        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        var scaleType: Int? = null
        var thumbnail: Uri? = null
        var image: Uri? = null

        if (webcam.type == Webcam.WebcamType.VIEWSURF.nameType) {
            scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_CROP
            if (PreferencesAW.isWebcamsHighQuality(requireContext()) && !webcam.mediaViewSurfHD.isNullOrEmpty() && !webcam.viewsurfHD.isNullOrEmpty()) {
                image = Uri.parse(String.format("%s/%s.jpg", webcam.viewsurfHD, webcam.mediaViewSurfHD))
            } else if (!webcam.mediaViewSurfLD.isNullOrEmpty() && !webcam.viewsurfLD.isNullOrEmpty()) {
                image = Uri.parse(String.format("%s/%s.jpg", webcam.viewsurfLD, webcam.mediaViewSurfLD))
            }
        } else {
            if (PreferencesAW.isWebcamsHighQuality(requireContext()) && !webcam.imageHD.isNullOrBlank()) {
                scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_CROP
                thumbnail = Uri.parse(webcam.imageLD)
                image = Uri.parse(webcam.imageHD)
            } else if (!webcam.imageLD.isNullOrBlank()) {
                image = Uri.parse(webcam.imageLD)
                scaleType = BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE
            }
        }

        scaleType?.also { bigImageViewWebcamImage.setInitScaleType(it) }

        image?.also {
            thumbnail?.also {
                bigImageViewWebcamImage.showImage(thumbnail, image)
            } ?: bigImageViewWebcamImage.showImage(image)
        }
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
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

            chooser.resolveActivity(requireActivity().packageManager)?.also {
                startActivity(chooser)
            } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

    override fun showProgress() {
        progressBarWebcamImageDetail.show()
    }
}