package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.item_search.view.*


/**
 * Created by laura on 05/12/2017.
 */
class AdapterSearch(val context: Context, var items: List<Webcam>, val listener: ((Webcam) -> Unit)? = null) : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val webcam = items.get(position)

        holder?.itemView?.textViewNameWebcam?.text = webcam.title ?: ""

        val urlWebCam: String
        if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            urlWebCam = String.format("%s/%s.jpg", webcam.viewsurfLD ?: "", webcam.mediaViewSurfLD ?: "")
        } else {
            urlWebCam = webcam.imageLD ?: ""
        }

        GlideApp.with(context).load(urlWebCam)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder?.itemView?.imageViewCamera)

        holder?.itemView?.setOnClickListener {
            listener?.invoke(webcam)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

}