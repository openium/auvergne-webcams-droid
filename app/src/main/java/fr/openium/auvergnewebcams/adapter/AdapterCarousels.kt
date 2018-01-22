package fr.openium.auvergnewebcams.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.LongSparseArray
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
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.header_list_webcam.view.*
import kotlinx.android.synthetic.main.item_carousel.view.*
import java.util.*

/**
 * Created by laura on 23/03/2017.
 */
class AdapterCarousels(val context: Context,
                       val listener: ((Webcam, Int) -> Unit)? = null,
                       var items: List<Section>,
                       val composites: CompositeDisposable,
                       var sectionFavoris: Section,
                       val listenerSectionClick: ((Section) -> Unit),
                       var lastUpdate: Long) : RecyclerView.Adapter<AdapterCarousels.WebcamHolder>() {

    private val heightImage: Int
    private var positionsAdapters = LongSparseArray<Int>()
    private var listeners = LongSparseArray<DiscreteScrollView.OnItemSelectedListener>()

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return WebcamHolder(view)
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {

        val item: Section
        if (position == 0) {
            item = sectionFavoris
        } else {
            item = items.get(position - 1)
        }

        if (item.webcams.isEmpty()) {
            holder.mTextViewNameSection.gone()
            holder.mTextViewNameWebcam.gone()
            holder.mTextViewNbCameras.gone()
            holder.mImageViewSection.gone()
            holder.mLinearLayoutSection.gone()
            holder.scrollView.gone()
        } else {
            holder.mTextViewNameSection.show()
            holder.mTextViewNameWebcam.show()
            holder.mTextViewNbCameras.show()
            holder.mImageViewSection.show()
            holder.mLinearLayoutSection.show()
            holder.scrollView.show()

            val section = item.title

            holder.mLinearLayoutSection.setOnClickListener {
                listenerSectionClick.invoke(item)
            }
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
                val pos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealRealPosition(holder.scrollView.currentItem)
                val webcam = item.webcams.get(pos)
                if (webcam != null) {
                    listener?.invoke(webcam, position)
                }
            }

            val adapter = AdapterWebcamsCarousel(context, listener, item, composites, lastUpdate)
            val infiniteAdapter = InfiniteScrollAdapter.wrap(adapter)
            holder.scrollView.adapter = infiniteAdapter

            val listener: DiscreteScrollView.OnItemSelectedListener
            if (listeners.get(item.uid) != null) {
                listener = listeners.get(item.uid)
            } else {
                listener = object : DiscreteScrollView.OnItemSelectedListener {
                    override fun onItemSelectedChanged(position: Int) {
//                        Timber.d("addItemSelectedListener ${item.uid}  newPosition: $position")
                        positionsAdapters.put(item.uid, position)

                        val realPosition = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealRealPosition(position)
                        if (realPosition >= 0 && item.webcams.size > realPosition) {
                            val name = item.webcams[realPosition]!!.title
                            holder.mTextViewNameWebcam.setText(name)
                        } else {
                            holder.mTextViewNameWebcam.setText("")
                        }
                    }
                }
                listeners.put(item.uid, listener)
                holder.scrollView.addItemSelectedListener(listener)
            }

            holder.scrollView.post {
                val positionAdapter: Int
                if (positionsAdapters.get(item.uid, -1) != -1) {
                    positionAdapter = positionsAdapters.get(item.uid)
                } else {
                    positionAdapter = holder.scrollView.currentItem
                }

                // Timber.d("addItemSelectedListener ${item.uid}  getPosition: $positionAdapter")

                val realPos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealRealPosition(positionAdapter)
                if (realPos >= 0 && item.webcams.size > realPos) {
                    val name = item.webcams[realPos]!!.title
                    holder.mTextViewNameWebcam.setText(name)
                } else {
                    holder.mTextViewNameWebcam.setText("")
                }
                holder.scrollView.layoutManager.scrollToPosition(positionAdapter)
//                Timber.e("scroll to position  ${item.uid}   $positionAdapter")
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTextViewNameSection: TextView
        val mTextViewNameWebcam: TextView
        val mTextViewNbCameras: TextView
        val mTextViewNbCamerasArrow: TextView
        val mImageViewSection: ImageView
        val mLinearLayoutSection: LinearLayout
        val scrollView: DiscreteScrollView

        init {
            mTextViewNameWebcam = view.textViewNameWebcam
            mTextViewNameSection = view.textViewNameSection
            mTextViewNbCameras = view.textViewNbCameras
            mTextViewNbCamerasArrow = view.textViewNbCamerasArrow
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

            mTextViewNbCamerasArrow.show()
        }
    }
}