package fr.openium.auvergnewebcams.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.item_section_webcams.view.*


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterMainSectionWebcams(
    prefUtils: PreferencesUtils,
    private val dateUtils: DateUtils,
    private var glideRequest: RequestManager,
    private var webcams: List<Webcam>,
    private val onWebcamClicked: ((Webcam) -> Unit)
) : RecyclerView.Adapter<AdapterMainSectionWebcams.WebcamHolder>() {

    private var mediaStoreSignature = MediaStoreSignature("", prefUtils.lastUpdateWebcamsTimestamp, 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_section_webcams, parent, false))
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val webcam = webcams[position % webcams.count()]

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
                isFirst: Boolean
            ): Boolean {
                holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_CROP
                updateErrorText(webcam, holder)
                holder.itemView.progressBarSectionWebcams.gone()
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                updateErrorText(webcam, holder, true)
                holder.itemView.progressBarSectionWebcams.gone()
                return false
            }
        }

        glideRequest.load(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
            .transition(DrawableTransitionOptions.withCrossFade())
            .signature(mediaStoreSignature)
            .listener(listenerGlide)
            .error(R.drawable.ic_broken_camera)
            .into(holder.itemView.imageViewSectionWebcamsImage)

        holder.itemView.setOnClickListener {
            onWebcamClicked(webcam)
        }
    }

    private fun updateErrorText(webcam: Webcam, holder: WebcamHolder, isFullError: Boolean = false) {
        when {
            isFullError -> {
                holder.itemView.textViewSectionWebcamsError.text = holder.itemView.context.getString(R.string.load_webcam_error)
                holder.itemView.textViewSectionWebcamsError.show()
            }
            webcam.isUpToDate(dateUtils) -> {
                holder.itemView.textViewSectionWebcamsError.gone()
            }
            else -> {
                holder.itemView.textViewSectionWebcamsError.text = holder.itemView.context.getString(R.string.generic_not_up_to_date)
                holder.itemView.textViewSectionWebcamsError.show()
            }
        }
    }

    override fun getItemCount(): Int = webcams.count()

    fun refreshData(data: List<Webcam>) {
        webcams = data
        notifyDataSetChanged()
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view)
}