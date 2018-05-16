package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import fr.openium.auvergnewebcams.BuildConfig
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.ext.fromIOToMain
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmList
import io.realm.RealmObject
import kotlinx.android.synthetic.main.item_carousel_webcam.view.*


/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcamsCarousel(val context: Context,
                             val listener: ((Webcam, Int) -> Unit)? = null,
                             var webcams: RealmList<Webcam>,
                             val composites: CompositeDisposable,
                             var lastUpdate: Long)
    : RecyclerView.Adapter<AdapterWebcamsCarousel.WebcamHolder>() {

    val heightImage: Int
    val widthImage: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
        widthImage = context.resources.getDimensionPixelOffset(R.dimen.width_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_webcam, parent, false)
        return WebcamHolder(view)
//                .apply {
//            layoutParams = ViewGroup.LayoutParams(widthImage, heightImage)
//        }
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = webcams.get(position)
        composites.add(Events.eventCameraDateUpdate
                .obs
                .fromIOToMain()
                .subscribe {
                    if (item != null && RealmObject.isValid(item) && it == item.uid) {
                        item.realm?.refresh()
                        //    Timber.e("uid = ${item.uid}")
                        this.notifyItemChanged(position)
                    }
                })

        val urlWebCam: String = item?.getUrlForWebcam(false, false) ?: ""
        val isUp = item?.isUpToDate() ?: true

        if (isUp) {
            holder.itemView.textviewWebcamNotUpdate.gone()
        } else {
            holder.itemView.textviewWebcamNotUpdate.setText(context.getString(R.string.generic_not_up_to_date))
            holder.itemView.textviewWebcamNotUpdate.show()
        }

        holder.itemView.progressbar.show()

//        Timber.e("load image section ${section.uid}    $position => $urlWebCam")
        val listenerGlide = object : RequestListener<Drawable> {
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

        }

        // Timber.e("DATE UPDATE $lastUpdate")
//        if (lastUpdate == 0L) { // no cache
//            GlideApp.with(context)
//                    .load(urlWebCam)
//                    .error(R.drawable.broken_camera)
//                    .listener(listenerGlide)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(true)
//                    .override(widthImage, heightImage)
//                    .into(holder.itemView.imageViewCamera)
//        } else {
        GlideApp.with(context)
                .load(urlWebCam)
                .error(R.drawable.broken_camera)
                .listener(listenerGlide)
                .signature(MediaStoreSignature("", lastUpdate, 0))
                .override(widthImage, heightImage)
                .into(holder.itemView.imageViewCamera)
//        }


        holder.itemView.setOnClickListener {
            if (item != null)
                listener?.invoke(item, position)
        }

        if (BuildConfig.DEBUG) {
            if (item?.lastUpdate ?: 0 > 0L) {
                val date = DateUtils.getDateFormatDateHour(item?.lastUpdate ?: 0)
                holder.itemView.textviewWebcamLastUpdate.setText(context.getString(R.string.generic_last_update, date))
                holder.itemView.textviewWebcamLastUpdate.show()
            } else {
                holder.itemView.textviewWebcamLastUpdate.gone()
            }
        } else {
            holder.itemView.textviewWebcamLastUpdate.gone()
        }
    }

    override fun getItemCount(): Int {
        return webcams.size
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view)
}