package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.item_carousel.view.*

/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcamCarousel(val context: Context, val listener: ((Webcam, Int) -> Unit)? = null, val items: List<Webcam>) : RecyclerView.Adapter<AdapterWebcamCarousel.WebcamHolder>() {

    val heightImage: Int
    val padding: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
        padding = context.resources.getDimensionPixelOffset(R.dimen.padding_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return WebcamHolder(view.apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightImage).apply {
                setPadding(padding, 0, padding, 0)
            }
        })
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = items.get(position)
        val urlWebCam: String = item.getUrlForWebcam(false, false)
        val isUp = item.isUpToDate()

        if(isUp) {
            holder.itemView.textviewWebcamNotUpdate.gone()
        } else {
            holder.itemView.textviewWebcamNotUpdate.show()
        }

        GlideApp.with(context).load(urlWebCam)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.itemView.imageViewCamera)

        holder.itemView.setOnClickListener {
            listener?.invoke(item, position)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}