package fr.openium.auvergnewebcams.ui.sectionDetail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.ImageUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.header_section.view.*
import kotlinx.android.synthetic.main.item_webcam.view.*
import java.util.*


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterSectionDetail(
    prefUtils: PreferencesUtils,
    private val dateUtils: DateUtils,
    private var glideRequest: RequestManager,
    private var data: List<Data>,
    private val onWebcamClicked: ((Webcam) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SECTION_VIEWTYPE = 0
        const val WEBCAM_VIEWTYPE = 1
    }

    private var mediaStoreSignature = MediaStoreSignature("", prefUtils.lastUpdateWebcamsTimestamp, 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SECTION_VIEWTYPE -> SectionHolder(inflater.inflate(R.layout.header_section, parent, false))
            WEBCAM_VIEWTYPE -> WebcamHolder(inflater.inflate(R.layout.item_webcam, parent, false))
            else -> error("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        when (holder) {
            is SectionHolder -> {
                holder.bindView(item.section)
            }
            is WebcamHolder -> {
                holder.bindView(item.webcam, onWebcamClicked, glideRequest, mediaStoreSignature, dateUtils)
            }
            else -> {

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].section?.let {
            SECTION_VIEWTYPE
        } ?: WEBCAM_VIEWTYPE
    }

    override fun getItemCount(): Int = data.count()

    fun refreshData(data: List<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    data class Data(
        var section: Section? = null,
        var webcam: Webcam? = null
    )

    class SectionHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(section: Section?) {
            section?.let {
                // Set section title
                itemView.textViewSectionName.text = section.title

                // Remove arrow to indicate it's not clickable
                itemView.textViewSectionNbCameras.setCompoundDrawables(null, null, null, null)

                // Image name have "-" instead of "_", we need to do the change
                val imageResourceID = ImageUtils.getImageResourceAssociatedToSection(itemView.context, section)

                // Set the right section icon
                Glide.with(itemView.context)
                    .load(imageResourceID)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.imageViewSection)

                // Set the number of camera to show in the subtitle
                val nbWebCams = String.format(
                    Locale.getDefault(),
                    itemView.context.resources.getQuantityString(
                        R.plurals.nb_cameras_format,
                        section.webcams.count(),
                        section.webcams.count()
                    )
                )
                itemView.textViewSectionNbCameras.text = nbWebCams
            }
        }
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(
            webcam: Webcam?,
            onWebcamClicked: (Webcam) -> Unit,
            glideRequest: RequestManager,
            mediaStoreSignature: MediaStoreSignature,
            dateUtils: DateUtils
        ) {
            webcam?.let {
                itemView.textViewWebcamName.text = webcam.title ?: ""

                // Show error text if needed
                updateErrorText(dateUtils, webcam)

                val listenerGlide = object : RequestListener<Drawable> {

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirst: Boolean
                    ): Boolean {
                        itemView.imageViewWebcamImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        updateErrorText(dateUtils, webcam)
                        itemView.progressBarWebcam.gone()
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemView.imageViewWebcamImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        updateErrorText(dateUtils, webcam, true)
                        itemView.progressBarWebcam.gone()
                        return false
                    }
                }

                glideRequest.load(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .signature(mediaStoreSignature)
                    .listener(listenerGlide)
                    .error(R.drawable.ic_broken_camera)
                    .into(itemView.imageViewWebcamImage)

                itemView.setOnClickListener {
                    onWebcamClicked.invoke(webcam)
                }
            }
        }

        private fun updateErrorText(dateUtils: DateUtils, webcam: Webcam, isFullError: Boolean = false) {
            when {
                isFullError -> {
                    itemView.textViewWebcamError.text = itemView.context.getString(R.string.load_webcam_error)
                    itemView.textViewWebcamError.show()
                }
                webcam.isUpToDate(dateUtils) -> {
                    itemView.textViewWebcamError.gone()
                }
                else -> {
                    itemView.textViewWebcamError.text = itemView.context.getString(R.string.generic_not_up_to_date)
                    itemView.textViewWebcamError.show()
                }
            }
        }
    }
}