package fr.openium.auvergnewebcams.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.api.load
import coil.target.Target
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.goneWithAnimationCompat
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.item_section_webcams.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterMainSectionWebcams(
    private var webcams: List<Webcam>,
    private val onWebcamClicked: ((Webcam) -> Unit)
) : RecyclerView.Adapter<AdapterMainSectionWebcams.WebcamHolder>(), KoinComponent {

    //    private var mediaStoreSignature = MediaStoreSignature("", prefUtils.lastUpdateWebcamsTimestamp, 0)
    private val dateUtils by inject<DateUtils>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_section_webcams, parent, false))
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val webcam = webcams[position % webcams.count()]

        // Show error text if needed
        updateErrorText(webcam.lastUpdate, holder)

        Coil.load(holder.itemView.imageViewSectionWebcamsImage.context, webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)) {
            error(R.drawable.ic_broken_camera)

            target(object : Target {
                override fun onStart(placeholder: Drawable?) {
                    holder.itemView.progressBarSectionWebcams.show()
                }

                override fun onError(error: Drawable?) {
                    holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    holder.itemView.imageViewSectionWebcamsImage.setImageDrawable(error)
                    updateErrorText(webcam.lastUpdate, holder, true)
                    holder.itemView.progressBarSectionWebcams.goneWithAnimationCompat()
                }

                override fun onSuccess(result: Drawable) {
                    holder.itemView.imageViewSectionWebcamsImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    holder.itemView.imageViewSectionWebcamsImage.setImageDrawable(result)
                    updateErrorText(webcam.lastUpdate, holder)
                    holder.itemView.progressBarSectionWebcams.goneWithAnimationCompat()
                }
            })
        }

        holder.itemView.setOnClickListener {
            onWebcamClicked(webcam)
        }
    }

    private fun updateErrorText(lastUpdateTime: Long?, holder: WebcamHolder, isFullError: Boolean = false) {
        when {
            isFullError -> {
                holder.itemView.textViewSectionWebcamsError.text = holder.itemView.context.getString(R.string.loading_not_working_error)
                holder.itemView.textViewSectionWebcamsError.show()
            }
            dateUtils.isUpToDate(lastUpdateTime) -> {
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