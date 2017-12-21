package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.header_list_webcam.view.*
import kotlinx.android.synthetic.main.item_webcam.view.*
import java.util.*

/**
 * Created by laura on 05/12/2017.
 */
class AdapterWebcams(val context: Context, var items: List<Webcam>, val listener: ((Webcam) -> Unit)? = null, val composites: CompositeDisposable, var hearderSection: Section? = null) : RecyclerView.Adapter<AdapterWebcams.ViewHolder>() {

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
        if (hearderSection != null && position == 0) {
            val imageName = hearderSection?.imageName?.replace("-", "_") ?: ""
            val resourceId = context.resources.getIdentifier(imageName, "drawable", context.getPackageName())
            if (resourceId != -1 && resourceId != 0) {
                holder?.itemView?.imageViewSection?.setImageResource(resourceId)
            } else {
                holder?.itemView?.imageViewSection?.setImageResource(R.drawable.pdd_landscape)
            }
            holder?.itemView?.textViewNameSection?.text = hearderSection?.title ?: ""
            holder?.itemView?.textViewNbCameras?.text = String.format(Locale.getDefault(),
                    context.resources.getQuantityString(R.plurals.nb_cameras_format, hearderSection?.webcams?.count() ?: 0, hearderSection?.webcams?.count() ?: 0))
        } else {
            val webcam = items.get(position - if (hearderSection != null) 1 else 0)
            composites.add(Events.eventCameraDateUpdate
                    .obs
                    .fromIOToMain()
                    .subscribe {
                        if (it == webcam.uid) {
                            this.notifyItemChanged(position)
                        }
                    })

            holder?.itemView?.textViewNameWebcam?.text = webcam.title ?: ""

            val isUp = webcam.isUpToDate()

            if (isUp) {
                holder?.itemView?.textviewWebcamNotUpdate?.gone()
            } else {
                holder?.itemView?.textviewWebcamNotUpdate?.setText(context.getString(R.string.generic_not_up_to_date))
                holder?.itemView?.textviewWebcamNotUpdate?.show()
            }

            val urlWebCam: String = webcam.getUrlForWebcam(false, false)
            GlideApp.with(context)
                    .load(urlWebCam)
                    .error(R.drawable.broken_camera)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            holder?.itemView?.textviewWebcamNotUpdate?.setText(context.getString(R.string.load_webcam_error))
                            holder?.itemView?.textviewWebcamNotUpdate?.show()

                            holder?.itemView?.imageViewCamera?.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            holder?.itemView?.progressbar?.gone()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            if (isUp) {
                                holder?.itemView?.textviewWebcamNotUpdate?.gone()
                            }
                            holder?.itemView?.imageViewCamera?.scaleType = ImageView.ScaleType.CENTER_CROP
                            holder?.itemView?.progressbar?.gone()
                            return false
                        }

                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder?.itemView?.imageViewCamera)

            holder?.itemView?.setOnClickListener {
                listener?.invoke(webcam)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        if (viewType == ViewType.HEADER.type) {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.header_list_webcam, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_webcam, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return items.size + if (hearderSection != null) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (hearderSection != null && position == 0) {
            return ViewType.HEADER.type
        }
        return ViewType.ITEM.type
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    enum class ViewType(val type: Int) {
        HEADER(0),
        ITEM(1)
    }

}