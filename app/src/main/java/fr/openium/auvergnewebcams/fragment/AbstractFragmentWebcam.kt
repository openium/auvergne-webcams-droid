package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.service.ServiceUploadFile
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm


/**
 * Created by laura on 01/12/2017.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    protected var webcam: Webcam? = null

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

        val webcamDB = realm!!.where(Webcam::class.java)
                .equalTo(Webcam::uid.name, id)
                .findFirst()
        if (webcamDB != null) {
            webcam = realm!!.copyFromRealm(webcamDB)
            (activity as AppCompatActivity).supportActionBar?.title = webcam!!.title
            initWebCam()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_detail, menu)
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
                    }
                }
    }

    private fun saveWebCamPicture() {
        var urlSrc = ""
        val fileName: String
        val isImage: Boolean

        if (webcam!!.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                urlSrc = String.format("%s/%s.mp4", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
            } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                urlSrc = String.format("%s/%s.mp4", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
            }
            fileName = String.format("%s_%s.mp4", webcam!!.title ?: "", System.currentTimeMillis().toString())
            isImage = false
        } else {
            if (!webcam!!.imageHD.isNullOrBlank()) {
                urlSrc = webcam!!.imageHD!!
            } else if (!webcam!!.imageLD.isNullOrBlank()) {
                urlSrc = webcam!!.imageLD!!
            }
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

    private fun shareWebCam() {
        val subject = webcam?.title

        var url = ""
        if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            if (!webcam?.mediaViewSurfHD.isNullOrEmpty() && !webcam?.viewsurfHD.isNullOrEmpty()) {
                url = String.format("%s/%s.mp4", webcam!!.viewsurfHD!!, webcam!!.mediaViewSurfHD!!)
            } else if (!webcam?.mediaViewSurfLD.isNullOrEmpty() && !webcam?.viewsurfLD.isNullOrEmpty()) {
                url = String.format("%s/%s.mp4", webcam!!.viewsurfLD!!, webcam!!.mediaViewSurfLD!!)
            }
        } else {
            if (!webcam!!.imageHD.isNullOrBlank()) {
                url = webcam!!.imageHD!!
            } else if (!webcam!!.imageLD.isNullOrBlank()) {
                url = webcam!!.imageLD!!
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_TEXT, String.format("%s \n%s", subject, url))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(activity?.getPackageManager()) != null) {
            startActivity(intent)
        }

    }

    private fun getLastPictureOrVideoOfWebcam() {
        if (applicationContext.hasNetwork) {
            Observable.just(1)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        if (webcam?.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                            // load media ld
                            webcam!!.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam!!.viewsurfLD)
                            webcam!!.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam!!.viewsurfHD)
                            Realm.getDefaultInstance().use {
                                it.executeTransaction {
                                    it.insertOrUpdate(webcam!!)
                                }
                            }
                        }
                        activity?.runOnUiThread {
                            initWebCam()
                        }
                    }
        }
    }

    abstract fun initWebCam()
    abstract fun showProgress()

}


