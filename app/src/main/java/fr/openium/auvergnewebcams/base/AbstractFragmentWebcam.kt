package fr.openium.auvergnewebcams.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.like.LikeButton
import com.like.OnLikeListener
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.eventCameraFavoris
import fr.openium.auvergnewebcams.event.eventHasNetwork
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.service.DownloadWorker
import fr.openium.auvergnewebcams.ui.webcamDetail.ViewModelWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.setTitle
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.footer_webcam_detail.*
import kotlinx.android.synthetic.main.header_webcam_detail.*
import timber.log.Timber


/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    protected lateinit var viewModelWebcamDetail: ViewModelWebcamDetail

    protected lateinit var webcam: Webcam
    private var itemMenuRefresh: MenuItem? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelWebcamDetail = ViewModelProvider(this).get(ViewModelWebcamDetail::class.java)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.getLong(Constants.KEY_WEBCAM_ID)?.also {
            setListener(it)
        } ?: activity?.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
        itemMenuRefresh = menu.findItem(R.id.menu_refresh)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_refresh -> {
                AnalyticsUtils.webcamDetailRefreshed(requireContext())
                refreshWebcam()
                true
            }
            R.id.menu_share -> {
                AnalyticsUtils.shareWebcamClicked(requireContext())
                shareWebCam()
                true
            }
            R.id.menu_save -> {
                AnalyticsUtils.saveWebcamClicked(requireContext())
                checkPermissionSaveFile()
                true
            }
            R.id.menu_signal_problem -> {
                AnalyticsUtils.signalProblemClicked(requireContext())
                signalProblem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDisplay()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListener(webcamId: Long) {
        viewModelWebcamDetail.getWebcamSingle(webcamId)
            .fromIOToMain()
            .subscribe({
                it.value?.let {
                    webcam = it

                    updateDisplay()
                    setWebcam()
                } ?: activity?.finish()
            }, {
                Timber.e(it, "Error getting webcam from DB")
                activity?.finish()
            }).addTo(disposables)

        buttonWebcamDetailFavorite.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                setFavChanged(true)
            }

            override fun unLiked(likeButton: LikeButton) = setFavChanged(false)
        })

        eventHasNetwork.filter { it }.fromIOToMain().subscribe({
            if (::webcam.isInitialized) {
                resetWebcam()
            }
        }, { Timber.e(it, "Error when listening for network state changing") }).addTo(disposables)
    }

    private fun setFavChanged(isLiked: Boolean) {
        webcam.isFavoris = isLiked
        viewModelWebcamDetail.updateWebcam(webcam)

        eventCameraFavoris.accept(webcam.uid)
        AnalyticsUtils.favoriteClicked(requireContext(), webcam.title ?: "", webcam.isFavoris)
    }

    private fun getState(): State = when {
        webcam.isUpToDate(dateUtils) -> State.LOADED_UP_TO_DATE
        else -> State.LOADED_NOT_UP_TO_DATE
    }

    open fun updateDisplay(forcedState: State? = null) {
        // Screen title
        setTitle(webcam.title ?: "")

        // Like button
        buttonWebcamDetailFavorite.isLiked = webcam.isFavoris
        buttonWebcamDetailFavorite.show()

        // Last update date
        if (webcam.lastUpdate ?: 0 > 0L) {
            val date = dateUtils.getDateInFullFormat(webcam.lastUpdate ?: 0)
            textViewWebcamDetailLastUpdate.text = getString(R.string.generic_last_update_format, date)
            textViewWebcamDetailLastUpdate.show()
        } else {
            textViewWebcamDetailLastUpdate.gone()
        }

        when (resources.configuration.orientation to (forcedState ?: getState())) {
            Configuration.ORIENTATION_PORTRAIT to State.LOADED_UP_TO_DATE -> {
                textViewWebcamDetailErrorMessage.gone()
                frameLayoutWebcamDetailHeader.show()
            }
            Configuration.ORIENTATION_PORTRAIT to State.LOADED_NOT_UP_TO_DATE -> {
                textViewWebcamDetailErrorMessage.text = getString(R.string.generic_not_up_to_date)
                textViewWebcamDetailErrorMessage.show()
                frameLayoutWebcamDetailHeader.show()
            }
            Configuration.ORIENTATION_PORTRAIT to State.NOT_WORKING -> {
                textViewWebcamDetailErrorMessage.text = getString(R.string.load_webcam_error)
                textViewWebcamDetailErrorMessage.show()
                frameLayoutWebcamDetailHeader.show()
            }
            Configuration.ORIENTATION_LANDSCAPE to State.LOADED_UP_TO_DATE,
            Configuration.ORIENTATION_LANDSCAPE to State.LOADED_NOT_UP_TO_DATE,
            Configuration.ORIENTATION_LANDSCAPE to State.NOT_WORKING -> {
                frameLayoutWebcamDetailHeader.gone()
                textViewWebcamDetailErrorMessage.gone()
            }
        }
    }

    private fun checkPermissionSaveFile() {
        disposables.add(
            RxPermissions(requireActivity()).requestEach(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            saveWebcam()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // We can ask again
                        }
                        else -> {
                            // Denied forever
                            snackbar(getString(R.string.error_no_permisson), Snackbar.LENGTH_SHORT)
                        }
                    }
                })
    }

    private fun signalProblem() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.detail_signal_problem_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.detail_signal_problem_subject, webcam.title ?: ""))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.detail_signal_problem_body_format, webcam.title ?: "", webcam.uid.toString()))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

        chooser.resolveActivity(requireActivity().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_email_app, Snackbar.LENGTH_SHORT)
    }

    protected fun startService(urlSrc: String, isPhoto: Boolean, fileName: String) {
        if (requireContext().hasNetwork) {
            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<DownloadWorker>().apply {
                    setInputData(Data.Builder().apply {
                        putString(DownloadWorker.KEY_PATH_URL, urlSrc)
                        putBoolean(DownloadWorker.KEY_IS_PHOTO, isPhoto)
                        putString(DownloadWorker.KEY_FILENAME, fileName)
                    }.build())
                }.build()
            )
        } else {
            snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
        }
    }

    enum class State {
        LOADED_UP_TO_DATE,
        LOADED_NOT_UP_TO_DATE,
        NOT_WORKING
    }

    abstract fun setWebcam()

    abstract fun resetWebcam()

    abstract fun shareWebCam()

    abstract fun saveWebcam()

    abstract fun refreshWebcam()
}


