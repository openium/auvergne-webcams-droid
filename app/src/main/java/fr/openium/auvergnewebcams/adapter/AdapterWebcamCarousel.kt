package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam

/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcamCarousel(val context: Context, val listener: ((Webcam, Int) -> Unit)? = null, val items: List<Webcam>) : RecyclerView.Adapter<AdapterWebcamCarousel.WebcamHolder>() {

    val heightImage: Int
    val padding: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
        padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, context.resources.displayMetrics).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightImage).apply {
                setPadding(padding, 0, padding, 0)
            }
        })
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = items.get(position)
        val urlWebCam: String
        if (item.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            urlWebCam = String.format("%s/%s.jpg", item.viewsurfLD ?: "", item.mediaViewSurfLD ?: "")
        } else {
            urlWebCam = item.imageLD ?: ""
        }

        GlideApp.with(context).load(urlWebCam)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.itemView as ImageView)

        holder.itemView.setOnClickListener {
            listener?.invoke(item, position)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
        }
    }
}