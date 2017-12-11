package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.ext.fromIOToMain
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_carousel.view.*


/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcamCarousel(val context: Context, val listener: ((Webcam, Int) -> Unit)? = null, var items: List<Webcam>, val composites: CompositeDisposable) : RecyclerView.Adapter<AdapterWebcamCarousel.WebcamHolder>() {

    val heightImage: Int
    val widthImage: Int

    init {

        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
        widthImage = context.resources.getDimensionPixelOffset(R.dimen.width_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return WebcamHolder(view.apply {
            layoutParams = ViewGroup.LayoutParams(widthImage, heightImage)
        })
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = items.get(position)
        composites.add(Events.eventCameraDateUpdate
                .obs
                .fromIOToMain()
                .subscribe {
                    if (it == item.uid) {
                        //    Timber.e("uid = ${item.uid}")
                        this.notifyItemChanged(position)
//                        this.notifyDataSetChanged()
                    }
                })

        val urlWebCam: String = item.getUrlForWebcam(false, false)
        val isUp = item.isUpToDate()

        if (isUp) {
            holder.itemView.textviewWebcamNotUpdate.gone()
        } else {
            holder.itemView.textviewWebcamNotUpdate.setText(context.getString(R.string.generic_not_up_to_date))
            holder.itemView.textviewWebcamNotUpdate.show()
        }


        holder.itemView.progressbar.show()

        GlideApp.with(context)
                .load(urlWebCam)
                .error(R.drawable.broken_camera)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.itemView.textviewWebcamNotUpdate.setText(context.getString(R.string.load_webcam_error))
                        holder.itemView.textviewWebcamNotUpdate.show()

                        holder.itemView.imageViewCamera.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        holder.itemView.progressbar.gone()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (isUp) {
                            holder.itemView.textviewWebcamNotUpdate.gone()
                        } else {
                            holder.itemView.textviewWebcamNotUpdate.show()
                        }
                        holder.itemView.imageViewCamera.scaleType = ImageView.ScaleType.CENTER_CROP
                        holder.itemView.progressbar.gone()
                        return false
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(widthImage, heightImage)
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