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
    val padding: Int
    val widthScreen: Int

    init {

        padding = context.resources.getDimensionPixelOffset(R.dimen.padding_image_list)
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        widthScreen = size.x
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
        composites.add(Events.eventCameraDateUpdate
                .obs
                .fromIOToMain()
                .subscribe {
                    if (it == item.uid) {
                        this.notifyItemChanged(position)
                    }
                })

        val urlWebCam: String = item.getUrlForWebcam(false, false)
        val isUp = item.isUpToDate()

        if (isUp) {
            holder.itemView.textviewWebcamNotUpdate.gone()
        } else {
            holder.itemView?.textviewWebcamNotUpdate?.setText(context.getString(R.string.generic_not_up_to_date))
            holder.itemView.textviewWebcamNotUpdate.show()
        }


        GlideApp.with(context)
                .load(urlWebCam)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.itemView.progressbar.gone()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.itemView.progressbar.gone()
                        return false
                    }

                })
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