package fr.openium.auvergnewebcams.base

import android.content.Intent
import android.content.res.Configuration
import android.net.MailTo
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
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
import fr.openium.kotlintools.ext.*
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.footer_webcam_detail.*
import kotlinx.android.synthetic.main.header_webcam_detail.*
import kotlinx.android.synthetic.main.layout_webcam_not_connected.*
import kotlinx.android.synthetic.main.layout_webcam_not_working.*
import timber.log.Timber


/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    protected lateinit var viewModelWebcamDetail: ViewModelWebcamDetail

    protected lateinit var webcam: Webcam
    private var itemMenuRefresh: MenuItem? = null

    protected var wasLastTimeLoadingSuccessfull = true

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
                it.value?.ifLet {
                    webcam = it

                    updateDisplay()
                    setWebcam()
                }?.elseLet {
                    toast(R.string.detail_webcam_not_found, Toast.LENGTH_SHORT)
                    activity?.finish()
                }
            }, {
                Timber.e(it, "Error getting webcam from DB")
                activity?.finish()
            }).addTo(disposables)

        buttonWebcamDetailFavorite.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) = setFavChanged(true)
            override fun unLiked(likeButton: LikeButton) = setFavChanged(false)
        })

        linearLayoutWebcamDetailNotWorking.setOnClickListener {
            signalProblem()
        }

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
        !requireContext().hasNetwork -> State.NOT_CONNECTED
        !wasLastTimeLoadingSuccessfull -> State.NOT_WORKING
        dateUtils.isUpToDate(webcam.getLastUpdateDate()) -> State.LOADED_UP_TO_DATE
        else -> State.LOADED_NOT_UP_TO_DATE
    }

    open fun updateDisplay() {
        // Screen title
        setTitle(webcam.title ?: "")

        // Like button
        buttonWebcamDetailFavorite.isLiked = webcam.isFavoris
//        buttonWebcamDetailFavorite.show() //TODO

        // Last update date
        val lastUpdate = webcam.getLastUpdateDate()
        if (lastUpdate != null) {
            textViewWebcamDetailLastUpdate.text =
                getString(R.string.generic_last_update_format, dateUtils.getDateInFullFormat(lastUpdate))
            textViewWebcamDetailLastUpdate.showWithAnimationCompat()
        } else {
            frameLayoutWebcamDetailHeader.goneWithAnimationCompat() //TODO
            textViewWebcamDetailLastUpdate.goneWithAnimationCompat()
        }

        when (resources.configuration.orientation to getState()) {
            Configuration.ORIENTATION_PORTRAIT to State.LOADED_UP_TO_DATE -> {
                showDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                textViewWebcamDetailNotUpToDate.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_PORTRAIT to State.LOADED_NOT_UP_TO_DATE -> {
                showDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                textViewWebcamDetailNotUpToDate.showWithAnimationCompat()
            }
            Configuration.ORIENTATION_PORTRAIT to State.NOT_WORKING -> {
                hideDetailContent()
                linearLayoutWebcamDetailNotWorking.showWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                textViewWebcamDetailNotUpToDate.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_PORTRAIT to State.NOT_CONNECTED -> {
                hideDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.showWithAnimationCompat()
                textViewWebcamDetailNotUpToDate.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_LANDSCAPE to State.LOADED_UP_TO_DATE -> {
                showDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                frameLayoutWebcamDetailHeader.goneWithAnimationCompat()
                frameLayoutWebcamDetailFooter.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_LANDSCAPE to State.LOADED_NOT_UP_TO_DATE -> {
                showDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                frameLayoutWebcamDetailHeader.goneWithAnimationCompat()
                frameLayoutWebcamDetailFooter.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_LANDSCAPE to State.NOT_WORKING -> {
                hideDetailContent()
                linearLayoutWebcamDetailNotWorking.showWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.goneWithAnimationCompat()
                frameLayoutWebcamDetailHeader.goneWithAnimationCompat()
                frameLayoutWebcamDetailFooter.goneWithAnimationCompat()
            }
            Configuration.ORIENTATION_LANDSCAPE to State.NOT_CONNECTED -> {
                hideDetailContent()
                linearLayoutWebcamDetailNotWorking.goneWithAnimationCompat()
                linearLayoutWebcamDetailNotConnected.showWithAnimationCompat()
                frameLayoutWebcamDetailHeader.goneWithAnimationCompat()
                frameLayoutWebcamDetailFooter.goneWithAnimationCompat()
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
        AnalyticsUtils.signalProblemClicked(requireContext())

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            data = Uri.parse(
                MailTo.MAILTO_SCHEME + getString(R.string.detail_signal_problem_email)
                        + "?subject=" + getString(R.string.detail_signal_problem_subject, webcam.title ?: "")
                        + "&body=" + getString(R.string.detail_signal_problem_body_format, webcam.title ?: "", webcam.uid.toString())
            )

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
        NOT_WORKING,
        NOT_CONNECTED
    }

    abstract fun showDetailContent()

    abstract fun hideDetailContent()

    abstract fun setWebcam()

    abstract fun resetWebcam()

    abstract fun shareWebCam()

    abstract fun saveWebcam()

    abstract fun refreshWebcam()
}