package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.carousel.DiscreteScrollView
import fr.openium.auvergnewebcams.carousel.InfiniteScrollAdapter
import fr.openium.auvergnewebcams.carousel.transform.Pivot
import fr.openium.auvergnewebcams.carousel.transform.ScaleTransformer
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_carousel.view.*
import java.util.*

/**
 * Created by laura on 23/03/2017.
 */
class AdapterCarousels(val context: Context, val listener: ((Webcam, Int) -> Unit)? = null, var items: List<Section>, val composites: CompositeDisposable) : RecyclerView.Adapter<AdapterCarousels.WebcamHolder>() {

    val heightImage: Int

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
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
            val pos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(holder.scrollView.currentItem)
            val webcam = item.webcams.get(pos)
            if (webcam != null) {
                listener?.invoke(webcam, position)
            }
        }

        holder.scrollView.addOnItemChangedListener { _, adapterPosition ->
            val realPos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(adapterPosition)
            if (realPos >= 0 && item.webcams.size > realPos) {
                val name = item.webcams[realPos]!!.title
//                Timber.e("$name   $adapterPosition   $realPos")
                holder.mTextViewNameWebcam.setText(name)
            } else {
                holder.mTextViewNameWebcam.setText("")
            }
        }

        val adapter = AdapterWebcamsCarousel(context, listener, item.webcams, composites)
        val infiniteAdapter = InfiniteScrollAdapter.wrap(adapter)
        holder.scrollView.adapter = infiniteAdapter

        val realPos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(holder.scrollView.currentItem)
        if (realPos >= 0 && item.webcams.size > realPos) {
            val name = item.webcams[realPos]!!.title
            holder.mTextViewNameWebcam.setText(name)
        } else {
            holder.mTextViewNameWebcam.setText("")
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
        val scrollView: DiscreteScrollView

        init {
            mTextViewNameWebcam = view.textViewNameWebcam
            mTextViewNameSection = view.textViewNameSection
            mTextViewNbCameras = view.textViewNbCameras
            mImageViewSection = view.imageViewSection
            scrollView = view.scrollView
            scrollView.setItemTransformer(ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.9f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.CENTER)
                    .build())
            scrollView.setItemTransitionTimeMillis(400)
            scrollView.setSlideOnFling(true)
            mLinearLayoutSection = view.linearLayoutSection

        }
    }
}