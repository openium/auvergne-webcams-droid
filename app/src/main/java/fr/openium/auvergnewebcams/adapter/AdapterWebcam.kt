package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.item_webcam.view.*
import java.util.*

/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcam(val context: Context, val listener: ((Webcam, Int) -> Unit)? = null, var items: List<Section>) : RecyclerView.Adapter<AdapterWebcam.WebcamHolder>() {

    val heightImage: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_webcam, parent, false)
        view.recyclerView.apply {
            layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true).apply {
                setPostLayoutListener(CarouselZoomPostLayoutListener())
            }
            setHasFixedSize(true)
            addOnScrollListener(CenterScrollListener())
        }
        return WebcamHolder(view)
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = items.get(position)

        val section = item.title

        holder.mLinearLayoutSection.visibility = View.VISIBLE
        holder.mTextViewNameSection.setText(section)
        val imageName = item.imageName?.replace("-", "_") ?: ""
        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.getPackageName())
        if (resourceId != -1 && resourceId != 0) {
            holder.mImageViewSection.setImageResource(resourceId)
        } else {
            holder.mImageViewSection.setImageResource(R.drawable.pdd_landscape)
        }
        val nbWebCams = String.format(Locale.getDefault(), context.resources.getQuantityString(R.plurals.nb_cameras_format, item.webcams.count(), item.webcams.count()))
        holder.mTextViewNbCameras.text = nbWebCams

        holder.itemView.setOnClickListener {
            val pos = (holder.itemView.recyclerView.layoutManager as CarouselLayoutManager).centerItemPosition
            val webcam = item.webcams.get(pos)
            if (webcam != null) {
                listener?.invoke(webcam, position)
            }
        }
        holder.mRecyclerView.adapter = AdapterWebcamCarousel(context, listener, item.webcams)

        holder.onSelectedListener = { pos ->
            val name = item.webcams[pos]!!.title
            holder.mTextViewNameWebcam.setText(name)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTextViewNameSection: TextView
        val mTextViewNameWebcam: TextView
        val mTextViewNbCameras: TextView
        val mImageViewSection: ImageView
        val mLinearLayoutSection: LinearLayout
        val mRecyclerView: RecyclerView
        var onSelectedListener: ((Int) -> Unit)? = null

        init {
            mTextViewNameWebcam = view.textViewNameWebcam
            mTextViewNameSection = view.textViewNameSection
            mTextViewNbCameras = view.textViewNbCameras
            mImageViewSection = view.imageViewSection
            mRecyclerView = view.recyclerView
            mLinearLayoutSection = view.linearLayoutSection
            (view.recyclerView.layoutManager as CarouselLayoutManager).addOnItemSelectionListener { pos ->
                onSelectedListener?.invoke(pos)
            }
        }
    }
}