package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.ext.*
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.service.ServiceUploadFile
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_webcam.*
import kotlinx.android.synthetic.main.header_detail_camera.*


/**
 * Created by laura on 01/12/2017.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    protected var webcam: Webcam? = null

    protected var itemMenuRefresh: MenuItem? = null

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getLong(Constants.KEY_ID) ?: 0

        webcam = realm!!.where(Webcam::class.java)
                .equalTo(Webcam::uid.name, id)
                .findFirst()
        if (webcam != null) {
            (activity as AppCompatActivity).supportActionBar?.title = webcam!!.title
            webcam!!.addChangeListener<Webcam> { webcamChange ->
                webcam = webcamChange
                initDateLastUpdate()
            }
            initWebCam()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            textViewLastUpdate.show()
        } else {
            textViewLastUpdate.gone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webcam?.removeAllChangeListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_detail, menu)
        itemMenuRefresh = menu?.findItem(R.id.menu_refresh)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_refresh) {
            getLastPictureOrVideoOfWebcam()
            return true
        } else if (item?.itemId == R.id.menu_share) {
            shareWebCam()
            return true
        } else if (item?.itemId == R.id.menu_save) {
            checkPermissionSaveFile()
            return true
        } else if (item?.itemId == R.id.menu_signal_problem) {
            signalProblem()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun checkPermissionSaveFile() {
        val rxPermission = RxPermissions(activity!!)
        rxPermission.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        saveWebCamPicture()
                    } else {
                        this.snackbar(getString(R.string.error_no_permisson), Snackbar.LENGTH_SHORT)
                    }
                }
    }

    private fun saveWebCamPicture() {
        val urlSrc = webcam?.getUrlForWebcam(true, true) ?: ""
        val fileName: String
        val isImage: Boolean

        if (webcam!!.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            fileName = String.format("%s_%s.mp4", webcam!!.title ?: "", System.currentTimeMillis().toString())
            isImage = false
        } else {
            fileName = String.format("%s_%s.jpg", webcam!!.title ?: "", System.currentTimeMillis().toString())
            isImage = true
        }

        ServiceUploadFile.startServiceUploadFile(applicationContext, urlSrc, isImage, fileName)
    }

    private fun signalProblem() {
        val subject = getString(R.string.detail_signal_problem_subject, webcam?.title ?: "")
        val body = getString(R.string.detail_signal_problem_body, webcam?.title ?: "", webcam?.uid?.toString() ?: "")
        val emailDest = getString(R.string.detail_signal_problem_email)

        val intent = Intent(android.content.Intent.ACTION_SENDTO).apply {
            setType("text/plain")
            data = Uri.parse(String.format("mailto:%s", emailDest))
            putExtra(android.content.Intent.EXTRA_TEXT, body)
            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(activity?.getPackageManager()) != null) {
            startActivity(intent)
        }
    }

    abstract fun shareWebCam()

    private fun getLastPictureOrVideoOfWebcam() {
        if (applicationContext.hasNetwork) {
            showProgress()
            itemMenuRefresh?.isEnabled = false

            Observable.just(1)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        val id = arguments?.getLong(Constants.KEY_ID) ?: 0
                        Realm.getDefaultInstance().executeTransaction {
                            val webcamDB = it.where(Webcam::class.java)
                                    .equalTo(Webcam::uid.name, id)
                                    .findFirst()
                            if (webcamDB != null) {
                                if (webcamDB.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                    // load media ld
                                    webcamDB.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcamDB.viewsurfLD)
                                    webcamDB.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcamDB.viewsurfHD)
                                }
                                it.insertOrUpdate(webcamDB)
                            }
                        }

                        activity?.runOnUiThread {
                            initWebCam()
                        }
                    }
        }
    }

    protected open fun initWebCam() {
        if (isAlive) {
            initDateLastUpdate()
            checkBoxFavoris.setOnCheckedChangeListener(null)
            checkBoxFavoris.isChecked = webcam?.isFavoris == true
            checkBoxFavoris.setOnCheckedChangeListener { compoundButton, isChecked ->
                realm!!.executeTransaction {
                    webcam?.isFavoris = isChecked
                }
                Events.eventCameraFavoris.set(webcam?.uid ?: -1L)
            }
        }
    }

    private fun initDateLastUpdate() {
        if (isAlive) {
            if (webcam != null) {
                if (webcam!!.isUpToDate()) {
                    textviewWebcamNotUpdate.gone()
                } else {
                    textviewWebcamNotUpdate.setText(getString(R.string.generic_not_up_to_date))
                    textviewWebcamNotUpdate.show()
                }

                if (webcam!!.lastUpdate ?: 0 > 0L) {
                    val date = DateUtils.getDateFormatDateHour(webcam!!.lastUpdate!!)
                    textViewLastUpdate.setText(getString(R.string.generic_last_update, date))
                    textViewLastUpdate.show()
                } else {
                    textViewLastUpdate.gone()
                }
            }
        }
    }

    open protected fun onLoadWebcamError() {
        textviewWebcamNotUpdate.setText(getString(R.string.load_webcam_error))
        textviewWebcamNotUpdate.show()
    }

    abstract fun showProgress()

}


