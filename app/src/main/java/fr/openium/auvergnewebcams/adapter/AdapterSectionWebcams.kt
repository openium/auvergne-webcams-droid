package fr.openium.auvergnewebcams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_section_webcams.view.*


/**
 * Created by laura on 23/03/2017.
 */
class AdapterSectionWebcams(var webcams: RealmList<Webcam>, private val onWebcamClicked: ((Webcam) -> Unit)) :
    RecyclerView.Adapter<AdapterSectionWebcams.WebcamHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_section_webcams, parent, false))
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = webcams[position % webcams.size]

        item?.let { webcam ->
            //Do the webcam is up to date (Refreshed at least once in the last 48hours)
            val isUp = webcam.isUpToDate()

            //If the Webcam is up to date, hide the error text associated.
            if (isUp) {
                holder.itemView.textViewSectionWebcamsError.gone()
            } else {
                holder.itemView.textViewSectionWebcamsError.text = holder.itemView.context.getString(R.string.generic_not_up_to_date)
                holder.itemView.textViewSectionWebcamsError.show()
            }

            //Show the progressBar
            holder.itemView.progressBarSectionWebcams.show()

//        val urlWebCam: String = item.getUrlForWebcam(canBeHD = false, canBeVideo = false)
//        val listenerGlide = object : RequestListener<Drawable> {
//            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                holder.itemView.textviewWebcamNotUpdate.setText(context.getString(R.string.load_webcam_error))
//                holder.itemView.textviewWebcamNotUpdate.show()
//
//                holder.itemView.imageViewCamera.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                holder.itemView.progressbar.gone()
//                return false
//            }
//
//            override fun onResourceReady(
//                resource: Drawable?,
//                model: Any?,
//                target: Target<Drawable>?,
//                dataSource: DataSource?,
//                isFirstResource: Boolean
//            ): Boolean {
//                if (isUp) {
//                    holder.itemView.textviewWebcamNotUpdate.gone()
//                } else {
//                    holder.itemView.textviewWebcamNotUpdate.show()
//                }
//                holder.itemView.imageViewCamera.scaleType = ImageView.ScaleType.CENTER_CROP
//                holder.itemView.progressbar.gone()
//                return false
//            }
//        }
//
//        GlideApp.with(context)
//            .load(urlWebCam)
//            .error(R.drawable.broken_camera)
//            .listener(listenerGlide)
//            .signature(MediaStoreSignature("", PreferencesAW.getLastUpdateWebcamsTimestamp(context), 0))
//            .override(
//                context.resources.getDimensionPixelOffset(R.dimen.width_image_list),
//                context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
//            )
//            .into(holder.itemView.imageViewCamera)

            holder.itemView.setOnClickListener {
                onWebcamClicked(webcam)
            }
        }
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view)
}