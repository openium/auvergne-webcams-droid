package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.model.Webcam

/**
 * Created by laura on 01/12/2017.
 */
abstract class AbstractFragmentWebcam : AbstractFragment() {

    protected var webcam: Webcam? = null

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getLong(Constants.KEY_ID) ?: 0

        webcam = realm!!.where(Webcam::class.java)
                .equalTo(Webcam::uid.name, id)
                .findFirst()
        (activity as AppCompatActivity).supportActionBar?.title = webcam!!.title
        initWebCam()
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    abstract fun initWebCam()

}