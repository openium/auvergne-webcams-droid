package fr.openium.auvergnewebcams.ui.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.dip
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.item_section_webcams.view.*


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterSectionWebcams(
    private val context: Context,
    private var webcams: List<Webcam>,
    private val onWebcamClicked: ((Webcam) -> Unit)
) :
    RecyclerView.Adapter<AdapterSectionWebcams.WebcamHolder>() {

    var mediaStoreSignature = MediaStoreSignature("", PreferencesAW.getLastUpdateWebcamsTimestamp(context), 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_section_webcams,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val webcam = webcams[position % webcams.size]

        // Show error text if needed
        updateErrorText(webcam, holder)

        // Show the progressBar
        holder.itemView.progressBarSectionWebcams.show()

        val listenerGlide = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                // Show error text if needed
                updateErrorText(webcam, holder)

                holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_CROP
                holder.itemView.progressBarSectionWebcams.gone()

                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                holder.itemView.textViewSectionWebcamsError.text = context.getString(R.string.load_webcam_error)
                holder.itemView.textViewSectionWebcamsError.show()

                holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                holder.itemView.progressBarSectionWebcams.gone()

                return false
            }
        }

        val urlWebCam: String = webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false)
        Glide.with(context)
            .load(urlWebCam)
            .thumbnail(0.5f)
            .override(dip(context, 220f).toInt(), dip(context, 150f).toInt())
            .signature(mediaStoreSignature)
            .error(R.drawable.broken_camera)
            .listener(listenerGlide)
            .into(holder.itemView.imageViewSectionWebcamsImage)

        holder.itemView.setOnClickListener {
            onWebcamClicked(webcam)
        }
    }

    private fun updateErrorText(webcam: Webcam, holder: WebcamHolder) {
        // Check if webcam is up to date (Means refreshed at least once in the last 48hours)
        val isUp = webcam.isUpToDate()

        // If the Webcam is up to date, hide the error text associated.
        if (isUp) {
            holder.itemView.textViewSectionWebcamsError.gone()
        } else {
            holder.itemView.textViewSectionWebcamsError.text = context.getString(R.string.generic_not_up_to_date)
            holder.itemView.textViewSectionWebcamsError.show()
        }
    }

    override fun getItemCount(): Int {
        return webcams.count()
    }

    fun refreshData(data: List<Webcam>) {
        webcams = data
        notifyDataSetChanged()
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view)
}