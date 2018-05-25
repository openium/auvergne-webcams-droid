package fr.openium.auvergnewebcams.adapter

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
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_carousel_webcam.view.*


/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcamsCarousel(val listener: ((Webcam, Int) -> Unit)? = null,
                             var webcams: RealmList<Webcam>) : RecyclerView.Adapter<AdapterWebcamsCarousel.WebcamHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_webcam, parent, false))
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = webcams.get(position)

        val context = holder.itemView.context

        val urlWebCam: String = item?.getUrlForWebcam(false, false) ?: ""
        val isUp = item?.isUpToDate() ?: true

        if (isUp) {
            holder.itemView.textviewWebcamNotUpdate.gone()
        } else {
            holder.itemView.textviewWebcamNotUpdate.setText(context.getString(R.string.generic_not_up_to_date))
            holder.itemView.textviewWebcamNotUpdate.show()
        }
        holder.itemView.progressbar.show()

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

        GlideApp.with(context)
                .load(urlWebCam)
                .error(R.drawable.broken_camera)
                .listener(listenerGlide)
                .signature(MediaStoreSignature("", PreferencesAW.getLastUpdateWebcamsTimestamp(context), 0))
                .override(context.resources.getDimensionPixelOffset(R.dimen.width_image_list), context.resources.getDimensionPixelOffset(R.dimen.height_image_list))
                .into(holder.itemView.imageViewCamera)

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