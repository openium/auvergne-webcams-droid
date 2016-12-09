package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants.KEY_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcams
import kotlinx.android.synthetic.main.fragment_webcam.*

/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentWebcam : AbstractFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_webcam

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getInt(KEY_ID) ?: 0
        val webcam = Webcams.list[id]
        Picasso.with(context).load(webcam.lqUrl).into(photoView)
    }
}