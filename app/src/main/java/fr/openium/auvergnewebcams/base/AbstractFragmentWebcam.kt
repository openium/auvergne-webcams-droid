package fr.openium.auvergnewebcams.base

import android.content.Intent
import android.content.res.Configuration
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.openium.auvergnewebcams.KEY_WEBCAM_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.eventHasNetwork
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.ext.lastUpdateDate
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.service.DownloadWorker
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ViewModelWebcamDetail
import fr.openium.auvergnewebcams.ui.webcamDetail.components.WebcamDetailScreen
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.setTitle
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.compose_view.*
import timber.log.Timber
import java.io.File


/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    override val layoutId: Int
        get() = R.layout.compose_view

    protected lateinit var viewModelWebcamDetail: ViewModelWebcamDetail

    private var itemMenuRefresh: MenuItem? = null

    protected var wasLastTimeLoadingSuccessful = true

    private var orientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)
    private var fileImage by mutableStateOf<File?>(null)

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelWebcamDetail = ViewModelProvider(this)[ViewModelWebcamDetail::class.java]
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getLong(KEY_WEBCAM_ID)?.also {
            viewModelWebcamDetail.setWebcamId(id = it)
        } ?: requireActivity().finish()



        composeView.setContent {
            AWTheme {
                val webcam by viewModelWebcamDetail.webcam.collectAsState()
                Timber.d("WEBCAM !!!!!")

                eventHasNetwork.filter { it }.fromIOToMain().subscribe({
                    refreshWebcam()
                }, { Timber.e(it, "Error when listening for network state changing") }).addTo(disposables)

                val state = getState(webcam)
                val isUpToDate = state != State.LOADED_NOT_UP_TO_DATE

                val isLowQualityOnly = if (isVideo()) {
                    webcam?.viewsurf.isNullOrEmpty()
                } else {
                    webcam?.imageHD.isNullOrEmpty()
                }

                setTitle(webcam?.title ?: "")

                if (state == State.NOT_WORKING) {
                    // TODO screen no working
                    //signalProblem = {
                    //                        signalProblem(webcam)
                    //                    }
                    // linearLayoutWebcamDetailNotWorking.setOnClickListener {
                    //                signalProblem()
                    //            }
                } else if (state == State.NOT_CONNECTED) {
                    // TODO screen not connected
                } else {
                    WebcamDetailScreen(
                        webcam = webcam,
                        isVideo = isVideo(),
                        dateUtils = dateUtils,
                        isLowQualityOnly = isLowQualityOnly,
                        isUpToDate = isUpToDate,
                        isWebcamsHighQuality = prefUtils.isWebcamsHighQuality,
                        setLastLoadingSuccess = {
                            wasLastTimeLoadingSuccessful = it
                        },
                        isOrientationPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                        onGetImageFile = { file ->
                            fileImage = file
                        }
                    )
                }

            }
        }

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

    abstract fun isVideo(): Boolean

    private fun setFavChanged(isLiked: Boolean) {
        /*  webcam.isFavorite = isLiked
          viewModelWebcamDetail.updateWebcam(webcam)
  
          eventCameraFavoris.accept(webcam.uid)
          AnalyticsUtils.favoriteClicked(requireContext(), webcam.title ?: "", webcam.isFavorite)*/
    }

    private fun getState(webcam: Webcam?): State =
        when {
            eventHasNetwork.value == false -> State.NOT_CONNECTED
            !wasLastTimeLoadingSuccessful -> State.NOT_WORKING
            dateUtils.isUpToDate(webcam?.lastUpdateDate) -> State.LOADED_UP_TO_DATE
            else -> State.LOADED_NOT_UP_TO_DATE
        }

    open fun updateDisplay() {
        orientation = resources.configuration.orientation
        /*
          // Like button
          buttonWebcamDetailFavorite.isLiked = webcam.isFavorite
          buttonWebcamDetailFavorite.show() //TODO
          }
        */
    }

    private fun checkPermissionSaveFile() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            saveWebcam()
        } else {
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
    }

    private fun signalProblem(webcam: Webcam?) {
        AnalyticsUtils.signalProblemClicked(requireContext())

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            data = Uri.parse(
                MailTo.MAILTO_SCHEME + getString(R.string.detail_signal_problem_email)
                        + "?subject=" + getString(R.string.detail_signal_problem_subject, webcam?.title ?: "")
                        + "&body=" + getString(R.string.detail_signal_problem_body_format, webcam?.title ?: "", webcam?.uid?.toString())
            )

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))
        chooser.resolveActivity(requireActivity().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_email_app, Snackbar.LENGTH_SHORT)
    }

    private fun startService(urlSrc: String, isPhoto: Boolean, fileName: String) {
        RxPermissions(requireActivity())
            .requestEach(android.Manifest.permission.POST_NOTIFICATIONS)
            .subscribe({ permission ->
                when {
                    permission.granted -> {
                        startWorker(urlSrc, isPhoto, fileName)
                    }

                    permission.shouldShowRequestPermissionRationale -> {
                        // We can ask again
                    }

                    else -> {
                        // Denied forever
                        snackbar(getString(R.string.error_no_permisson), Snackbar.LENGTH_SHORT)
                    }
                }

            }, {
                Timber.e(it)
            }).addTo(disposables)
    }

    private fun startWorker(urlSrc: String, isPhoto: Boolean, fileName: String) {
        if (eventHasNetwork.value == true) {
            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<DownloadWorker>().apply {
                    setInputData(Data.Builder().apply {
                        putString(DownloadWorker.KEY_PATH_URL, urlSrc)
                        putBoolean(DownloadWorker.KEY_IS_PHOTO, isPhoto)
                        putString(DownloadWorker.KEY_FILENAME, fileName)
                    }.build())
                }.build()
            )
        } else snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
    }

    private fun saveWebcam() {
        viewModelWebcamDetail.webcam.value?.let { webcam ->
            val urlSrc = if (isVideo()) {
                webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
            } else {
                webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)
            }
            val fileName = if (isVideo()) {
                String.format("%s_%s.mp4", webcam.title ?: "", System.currentTimeMillis().toString())
            } else {
                String.format("%s_%s.jpg", webcam.title ?: "", System.currentTimeMillis().toString())
            }
            startService(urlSrc, !isVideo(), fileName)
        }
    }

    private fun refreshWebcam() {
        if (eventHasNetwork.value == true) {
            viewModelWebcamDetail.refreshWebCam()
        } else snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
    }


    private fun shareWebCam() {
        viewModelWebcamDetail.webcam.value?.let { webcam ->
            val chooser = if (isVideo()) {
                val url = webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, String.format("%s \n%s", webcam.title, url))
                    putExtra(Intent.EXTRA_SUBJECT, webcam.title)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                Intent.createChooser(intent, getString(R.string.generic_chooser))
            } else {
                fileImage?.let {
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

                    Intent.createChooser(intent, getString(R.string.generic_chooser))
                }
            }
            chooser?.resolveActivity(requireActivity().packageManager)?.also {
                startActivity(chooser)
            } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

    enum class State {
        LOADED_UP_TO_DATE,
        LOADED_NOT_UP_TO_DATE,
        NOT_WORKING,
        NOT_CONNECTED
    }

}