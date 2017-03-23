package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.model.adapter.ItemWebcam
import kotlinx.android.synthetic.main.item_webcam.view.*
import java.util.*

/**
 * Created by laura on 23/03/2017.
 */
class AdapterWebcam(val context: Context, val picasso: Picasso, val listener: ((Webcam, Int) -> Unit)? = null, val items: List<ItemWebcam>) : RecyclerView.Adapter<AdapterWebcam.WebcamHolder>() {

    val heightImage: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_webcam, parent, false))
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        val item = items.get(position)

        val section = item.nameSection
        val hasHeader: Boolean
        if (position == 0) {
            hasHeader = true
        } else {
            val prevItem = items.get(position - 1)
            hasHeader = section != prevItem.nameSection
        }

        val webCam = item.webcam
        val urlWebCam = webCam.imageLD
        val nameWebCam = webCam.title

        picasso.load(urlWebCam)
                .fit()
                .into(holder.mImageViewWebCam)

        holder.mTextViewNameWebcam.setText(nameWebCam)
        if (hasHeader) {
            holder.mLinearLayoutSection.visibility = View.VISIBLE
            holder.mViewSeparator.visibility = View.GONE
            holder.mTextViewNameSection.setText(section)
            val resourceId = context.resources.getIdentifier(item.imageSection, "drawable", context.getPackageName())
            if(resourceId != -1) {
                holder.mImageViewSection.setImageResource(resourceId)
            } else {
                holder.mImageViewSection.setImageResource(R.drawable.pdd_landscape)
            }
            val nbWebCams = String.format(Locale.getDefault(), context.resources.getQuantityString(R.plurals.nb_cameras_format, item.nbWebcams, item.nbWebcams))
            holder.mTextViewNbCameras.text = nbWebCams
        } else {
            holder.mLinearLayoutSection.visibility = View.GONE
            holder.mViewSeparator.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            listener?.invoke(webCam, position)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTextViewNameSection: TextView
        val mTextViewNameWebcam: TextView
        val mTextViewNbCameras: TextView
        val mImageViewWebCam: ImageView
        val mImageViewSection: ImageView
        val mLinearLayoutSection: LinearLayout
        val mViewSeparator: View

        init {
            mTextViewNameWebcam = view.textViewNameWebcam
            mTextViewNameSection = view.textViewNameSection
            mTextViewNbCameras = view.textViewNbCameras
            mImageViewWebCam = view.imageViewWebCam
            mImageViewSection = view.imageViewSection
            mLinearLayoutSection = view.linearLayoutSection
            mViewSeparator = view.viewSeparator
        }
    }
}