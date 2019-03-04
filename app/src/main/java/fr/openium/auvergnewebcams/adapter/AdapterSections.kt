package fr.openium.auvergnewebcams.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import cdflynn.android.library.turn.TurnLayoutManager
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.utils.ImageUtils
import fr.openium.kotlintools.ext.dip
import kotlinx.android.synthetic.main.header_section.view.*
import kotlinx.android.synthetic.main.item_section.view.*
import java.util.*


/**
 * Created by laura on 23/03/2017.
 */
class AdapterSections(
    private val sections: ArrayList<Section>,
    private val onSectionClicked: ((Section) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_SECTION = 0
        const val ITEM_SECTION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_SECTION -> HeaderSectionHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_section, parent, false))
            ITEM_SECTION -> ItemSectionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false))
            else -> {
                error("type unknown $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context

        val item = sections[position]
        when (holder) {
            is HeaderSectionHolder -> {
                //Set section title
                holder.itemView.textViewSectionName.text = item.title

                //Image name have "-" instead of "_", we need to do the change
                val imageResourceID = ImageUtils.getImageResourceAssociatedToSection(context, item)

                //Set the right section icon
                holder.itemView.imageViewSection.setImageResource(imageResourceID)

                //Set the number of camera to show in the subtitle
                val nbWebCams = String.format(
                    Locale.getDefault(),
                    context.resources.getQuantityString(R.plurals.nb_cameras_format, item.webcams.count(), item.webcams.count())
                )
                holder.itemView.textViewSectionNbCameras.text = nbWebCams

                //Set on click on Section part
                holder.itemView.linearLayoutSection.setOnClickListener {
                    onSectionClicked.invoke(item)
                }
            }
            is ItemSectionHolder -> {
                //Create and set adapter only if this is not already done
                if (holder.itemView.recyclerViewWebcams.adapter == null) {
                    val adapter = AdapterSectionWebcams(item.webcams) {
                        //TODO
//                      FragmentCarouselWebcam.startActivityDetailCamera(context, item)
                    }

                    //Creating the layoutManager where magic happened
                    val layoutManager = TurnLayoutManager.Builder(context)
                        .setRadius(20000)
                        .setMinScale(1f)
                        .setMaxScale(1.2f)
                        .setMinAlpha(0.6f)
                        .setPeekDistance(0)
                        .setMaxAlpha(1f)
                        .setOrientation(TurnLayoutManager.Orientation.HORIZONTAL)
                        .setGravity(TurnLayoutManager.Gravity.END)
                        .build()

                    //Applying all settings to the RecyclerView
                    holder.itemView.recyclerViewWebcams.apply {
                        this.adapter = adapter
                        this.layoutManager = layoutManager

                        //Some optimization
                        setHasFixedSize(true)

                        //This is needed to scroll 1 by 1 (Just like paging)
                        PagerSnapHelper().attachToRecyclerView(this)

                        // This is needed because the PagerSnapHelper is not triggered when we use scrollToPosition(),
                        // it is by calling smoothScrollToPosition()but we don't want to see the scrolling animation,
                        // so we need to center manually by calculating the offset
                        layoutManager.scrollToPositionWithOffset(
                            Integer.MAX_VALUE / 2,
                            (Resources.getSystem().displayMetrics.widthPixels - dip(220).toInt()) / 2
                        )
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            HEADER_SECTION
        } else {
            ITEM_SECTION
        }
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    class HeaderSectionHolder(view: View) : RecyclerView.ViewHolder(view)

    class ItemSectionHolder(view: View) : RecyclerView.ViewHolder(view)
}