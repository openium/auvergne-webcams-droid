package fr.openium.auvergnewebcams.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.leochuan.ScaleLayoutManager
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.custom.CustomScaleLayoutManager
import fr.openium.auvergnewebcams.custom.SnapOnScrollListener
import fr.openium.auvergnewebcams.ext.attachSnapHelperWithListener
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import fr.openium.kotlintools.ext.dip
import kotlinx.android.synthetic.main.header_section.view.*
import kotlinx.android.synthetic.main.item_section.view.*


/**
 * Created by Openium on 19/02/2019.
 */
class AdapterMainSections(
    private val prefUtils: PreferencesUtils,
    private var data: List<Data>,
    private val onHeaderClicked: ((Pair<Long, String>) -> Unit),
    private val onItemClicked: ((Webcam) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW_TYPE = 0
        const val ITEM_VIEW_TYPE = 1
    }

    private val viewPool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(0, 30)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> HeaderHolder(inflater.inflate(R.layout.header_section, parent, false)) // TODO
            ITEM_VIEW_TYPE -> ItemHolder(inflater.inflate(R.layout.item_section, parent, false)) // TODO
            else -> error("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderHolder -> holder.bindView(data[position].header, onHeaderClicked)
            is ItemHolder -> holder.bindView(data[position].webcams, onItemClicked, viewPool, prefUtils)
            else -> {
                // Nothing to do
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (data[position].header != null) HEADER_VIEW_TYPE else ITEM_VIEW_TYPE

    override fun getItemCount(): Int = data.count()

    fun refreshData(dataList: List<Data>) {
        data = dataList
        notifyDataSetChanged()
    }

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(dataHeader: DataHeader?, onHeaderClicked: (Pair<Long, String>) -> Unit) {
            dataHeader?.let { header ->
                // Set section title
                itemView.textViewSectionName.text = header.title

                // Set the right section icon
                Glide.with(itemView.context)
                    .load(header.imageId)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.imageViewSection)

                // Set the number of camera to show in the subtitle
                itemView.textViewSectionNbCameras.text = header.nbWebCamsString

                // Set on click on Section part
                itemView.linearLayoutSection.setOnClickListener {
                    onHeaderClicked.invoke(header.sectionId to header.title)
                }
            }
        }
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(
            webcamList: List<Webcam>?,
            onItemClicked: (Webcam) -> Unit,
            viewPool: RecyclerView.RecycledViewPool,
            prefUtils: PreferencesUtils
        ) {
            webcamList?.let { webcams ->
                // Create and set adapter only if this is not already done
                if (itemView.recyclerViewWebcams.adapter == null) {

                    // Applying all settings to the RecyclerView
                    itemView.recyclerViewWebcams.apply {
                        adapter = AdapterMainSectionWebcams(prefUtils, Glide.with(itemView.context), webcams, onItemClicked)
                        layoutManager =
                            CustomScaleLayoutManager(itemView.context, dip(-40f).toInt(), 5f, ScaleLayoutManager.HORIZONTAL).apply {
                                minScale = 0.7f
                                minAlpha = 0.3f
                                maxAlpha = 1f
                                maxVisibleItemCount = 3
                                infinite = true
                                enableBringCenterToFront = true
                                setItemViewCacheSize(0)
                                recycleChildrenOnDetach = true
                            }

                        // Some optimization
                        setHasFixedSize(true)
                        setRecycledViewPool(viewPool)
                    }
                } else {
                    itemView.recyclerViewWebcams.also {
                        // Remove all previous listeners
                        it.clearOnScrollListeners()
                        it.onFlingListener = null

                        // Update webcams list
                        (it.adapter as AdapterMainSectionWebcams).refreshData(webcams)
                    }
                }

                // This is needed to scroll 1 by 1, and get notified about position changing
                itemView.recyclerViewWebcams.attachSnapHelperWithListener(
                    PagerSnapHelper(),
                    SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                    object : SnapOnScrollListener.OnSnapPositionChangeListener {
                        override fun onSnapPositionChange(position: Int) {
                            itemView.textViewWebcamName.text = webcams[position % webcams.count()].title
                        }
                    }
                )
            }
        }
    }

    data class Data(
        val header: DataHeader? = null,
        val webcams: List<Webcam>? = null
    )

    data class DataHeader(val sectionId: Long, val title: String, val imageId: Int, val nbWebCamsString: String)
}