package fr.openium.auvergnewebcams.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.model.Webcams
import kotlinx.android.synthetic.main.fragment_list_camera.*

/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment(), OnWebcamClickListener {


    override val layoutId: Int
        get() = R.layout.fragment_list_camera

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = AdapterWebcam(context, this)
    }


    override fun onWebcamClick(webcam: Webcam, position: Int) {
        val intent = Intent(context, ActivityWebcam::class.java)
        intent.putExtra(Constants.KEY_ID, position)
        startActivity(intent)
    }

    class AdapterWebcam(val context: Context, val listener: OnWebcamClickListener? = null) : RecyclerView.Adapter<AdapterWebcam.WebcamHolder>() {

        override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
            val webcam = Webcams.list[position]

            Picasso.with(context).load(webcam.lqUrl).into((holder.itemView as ImageView))
            holder.itemView.setOnClickListener {

                listener?.onWebcamClick(webcam, position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
            return WebcamHolder(View.inflate(parent.context, R.layout.item_webcam, null))
        }

        override fun getItemCount(): Int {
            return Webcams.list.size
        }


        class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }
}

interface OnWebcamClickListener {
    fun onWebcamClick(webcam: Webcam, position: Int)
}
