package fr.openium.auvergnewebcams.fragment

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.Constants.KEY_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragment() {
    private var webcam: Webcam? = null

    override val layoutId: Int
        get() = R.layout.fragment_webcam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BigImageViewer.initialize(FrescoImageLoader.with(activity.applicationContext));
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getLong(KEY_ID) ?: 0

        webcam = realm!!.where(Webcam::class.java)
                .equalTo(Webcam::uid.name, id)
                .findFirst()
        (activity as AppCompatActivity).supportActionBar?.title = webcam!!.title
        initWebCam()
    }

    private fun initWebCam() {
        if (isAlive) {
            val display = activity.getWindowManager().getDefaultDisplay()
            val size = Point()
            display.getSize(size)

            mBigImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_AUTO)

            if (!webcam!!.imageHD.isNullOrBlank()) {
                mBigImage.showImage(Uri.parse( webcam!!.imageLD!!), Uri.parse(webcam!!.imageHD!!))
            } else {
                mBigImage.showImage(Uri.parse( webcam!!.imageLD!!))
            }

        }
    }

}