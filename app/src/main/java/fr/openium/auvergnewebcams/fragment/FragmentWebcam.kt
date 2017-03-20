package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants.KEY_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_webcam.*

/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragment() {

    companion object {
        @JvmStatic val TAG = FragmentWebcam::class.java.simpleName
    }

    private var mWebCam: Webcam? = null

    override val layoutId: Int
        get() = R.layout.fragment_webcam

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getLong(KEY_ID) ?: 0
        mRealm.executeTransactionAsync (
                {
                    realm ->
                        val webCam = realm.where(Webcam::class.java)
                                .equalTo(Webcam::uid.name, id)
                                .findFirst()
                    if(webCam != null) {
                    mWebCam = realm.copyFromRealm(webCam)
                }
                },
                {
                    initWebCam()
                },
                {
                    error ->
                        Log.e(TAG, error.message)
                        initWebCam()
                }
        )
    }

    private fun initWebCam() {
        if(isAlive) {
            if(mWebCam != null && !TextUtils.isEmpty(mWebCam!!.imageLD)) {
                Picasso.with(context).load(mWebCam!!.imageLD)
                        .into(photoView)
            }
        }
    }

}