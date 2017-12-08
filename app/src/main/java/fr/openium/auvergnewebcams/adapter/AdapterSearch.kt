package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.item_search.view.*


/**
 * Created by laura on 05/12/2017.
 */
class AdapterSearch(val context: Context, var items: List<Webcam>, val listener: ((Webcam) -> Unit)? = null) : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {

    val heightImage: Int
    val widthScreen: Int

    init {

        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        widthScreen = size.x
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val webcam = items.get(position)

        holder?.itemView?.textViewNameWebcam?.text = webcam.title ?: ""

        val isUp = webcam.isUpToDate()

        if (isUp) {
            holder?.itemView?.textviewWebcamNotUpdate?.gone()
        } else {
            holder?.itemView?.textviewWebcamNotUpdate?.show()
        }


        val urlWebCam: String = webcam.getUrlForWebcam(false, false)

        GlideApp.with(context)
                .load(urlWebCam)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder?.itemView?.progressbar?.gone()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder?.itemView?.progressbar?.gone()
                        return false
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(widthScreen, heightImage)
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