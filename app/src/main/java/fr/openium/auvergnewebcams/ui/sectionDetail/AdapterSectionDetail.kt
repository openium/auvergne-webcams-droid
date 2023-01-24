package fr.openium.auvergnewebcams.ui.sectionDetail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.clear
import coil.dispose
import coil.load
import coil.request.ImageRequest
import coil.target.Target
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.ImageUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.goneWithAnimationCompat
import fr.openium.kotlintools.ext.show
import kotlinx.android.synthetic.main.header_section.view.*
import kotlinx.android.synthetic.main.item_webcam.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterSectionDetail(
    private var data: List<Data>,
    private val onWebcamClicked: ((Webcam) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {

    companion object {
        const val SECTION_VIEWTYPE = 0
        const val WEBCAM_VIEWTYPE = 1
    }

    private val dateUtils by inject<DateUtils>()

//    private var mediaStoreSignature = MediaStoreSignature("", prefUtils.lastUpdateWebcamsTimestamp, 0)

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
                holder.bindView(item.webcam, onWebcamClicked, dateUtils)
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

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.imageViewWebcamImage.dispose()
    }

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
                itemView.imageViewSection.load(imageResourceID)

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
            dateUtils: DateUtils
        ) {
            webcam?.let {
                itemView.textViewWebcamName.text = webcam.title ?: ""

                // Show error text if needed
                updateErrorText(dateUtils, webcam)

                val request = ImageRequest.Builder(itemView.context)
                    .data(webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false))
                    .error(R.drawable.ic_broken_camera)
                    .target(object : Target {
                        override fun onStart(placeholder: Drawable?) {
                            itemView.progressBarWebcam.show()
                        }

                        override fun onError(error: Drawable?) {
                            itemView.imageViewWebcamImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            itemView.imageViewWebcamImage.setImageDrawable(error)
                            updateErrorText(dateUtils, webcam, true)
                            itemView.progressBarWebcam.goneWithAnimationCompat()
                        }

                        override fun onSuccess(result: Drawable) {
                            itemView.imageViewWebcamImage.scaleType = ImageView.ScaleType.CENTER_CROP
                            itemView.imageViewWebcamImage.setImageDrawable(result)
                            updateErrorText(dateUtils, webcam)
                            itemView.progressBarWebcam.goneWithAnimationCompat()
                        }
                    }).build()

                Coil.imageLoader(context = itemView.context)
                    .enqueue(request)

                itemView.setOnClickListener {
                    onWebcamClicked.invoke(webcam)
                }
            }
        }

        private fun updateErrorText(dateUtils: DateUtils, webcam: Webcam, isFullError: Boolean = false) {
            when {
                isFullError -> {
                    itemView.textViewWebcamError.text = itemView.context.getString(R.string.loading_not_working_error)
                    itemView.textViewWebcamError.show()
                }
                dateUtils.isUpToDate(webcam.lastUpdate) -> {
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