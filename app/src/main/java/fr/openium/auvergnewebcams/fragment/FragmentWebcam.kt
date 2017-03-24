package fr.openium.auvergnewebcams.fragment

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
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
            val width = size.x
            val height = size.y
            if (!webcam!!.imageHD.isNullOrBlank()) {
//                oneTimeSubscriptions.add(Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
                Picasso.with(context)
                        .load(webcam!!.imageHD)
                        .resize(width, height)
                        .centerInside()
                        .into(photoView)
//                })
            } else {
                Picasso.with(context).load(webcam!!.imageLD)
                        .resize(width, height)
                        .centerInside()
                        .into(photoView)
            }

        }
    }

}