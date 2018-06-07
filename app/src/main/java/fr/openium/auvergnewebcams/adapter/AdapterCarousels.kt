package fr.openium.auvergnewebcams.adapter

import android.support.v4.util.LongSparseArray
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.carousel.DiscreteScrollView
import fr.openium.auvergnewebcams.carousel.InfiniteScrollAdapter
import fr.openium.auvergnewebcams.carousel.transform.Pivot
import fr.openium.auvergnewebcams.carousel.transform.ScaleTransformer
import fr.openium.auvergnewebcams.fragment.FragmentCarouselWebcam.Companion.startActivityDetailCamera
import fr.openium.auvergnewebcams.fragment.FragmentCarouselWebcam.Companion.startActivityListWebcam
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.auvergnewebcams.utils.WeatherUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import io.realm.RealmResults
import kotlinx.android.synthetic.main.header_list_webcam.view.*
import kotlinx.android.synthetic.main.item_carousel.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by laura on 23/03/2017.
 */
class AdapterCarousels(var sections: RealmResults<Section>,
                       var sectionFav: Section? = null,
                       var weatherList: RealmResults<Weather>? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var positionsAdapters = HashMap<Long, Int>()
    private var selectedListeners = LongSparseArray<DiscreteScrollView.OnItemSelectedListener>()

    val ITEM_FAV_SECTION = 0
    val ITEM_OTHER_SECTION = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_FAV_SECTION -> FavSectionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false))
            ITEM_OTHER_SECTION -> SectionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false))
            else -> {
                error("type unknown $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context

        val item: Section?
        if (holder is FavSectionHolder) {
            item = sectionFav!!
        } else {
            if (sectionFav != null) {
                item = sections.get(position - 1)
            } else {
                item = sections.get(position)
            }
        }

        item?.let {
            //Set view display
            holder.itemView.textViewNameSection.setText(item.title)

            //Image name have - instead of _, we need to do the change
            val imageName = item.imageName?.replace("-", "_") ?: ""

            //Get resources with the new name
            val resourceId = context.resources.getIdentifier(imageName, "drawable", context.getPackageName())
            if (resourceId != -1 && resourceId != 0) {
                Glide.with(context).load(resourceId).into(holder.itemView.imageViewSection)
            } else {
                Glide.with(context).load(R.drawable.pdd_landscape).into(holder.itemView.imageViewSection)
            }

            //Set the number of camera to show in the subtitle
            val nbWebCams = String.format(Locale.getDefault(), context.resources.getQuantityString(R.plurals.nb_cameras_format, item.webcams.count(), item.webcams.count()))
            holder.itemView.textViewNbCameras.text = nbWebCams

            //Set the Weather
            val weather = weatherList?.filter {
                it.lat == item.latitude && it.lon == item.longitude
            }?.firstOrNull()

            if (weather != null && PreferencesAW.getIfWeatherCouldBeDisplayed(context)) {
                Glide.with(context).load(WeatherUtils.weatherImage(weather.id)).into(holder.itemView.imageViewSectionWeather)
                holder.itemView?.textViewSectionWeather?.setText(context.getString(R.string.weather_celcius, WeatherUtils.convertKelvinToCelcius(weather.temp)))
                holder.itemView.imageViewSectionWeather.show()
                holder.itemView.textViewSectionWeather.show()
            } else {
                holder.itemView.imageViewSectionWeather.gone()
                holder.itemView.textViewSectionWeather.gone()
            }

            //Set on click on Section part
            holder.itemView.linearLayoutSection.setOnClickListener {
                startActivityListWebcam(context, item)
            }

            //Set on click on any title of webcam (to do the same as if we click on the webcam)
            holder.itemView.setOnClickListener {
                val pos = (holder.itemView.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(holder.itemView.scrollView.currentItem)
                val webcam = item.webcams.get(pos)
                if (webcam != null) {
                    startActivityDetailCamera(context, webcam)
                }
            }

            //Set adapter of Horizontal scrollView
            if (holder.itemView.scrollView.adapter == null) {
                val adapter = AdapterWebcamsCarousel(item.webcams)
                val infiniteAdapter = InfiniteScrollAdapter.wrap(adapter)
                holder.itemView.scrollView.adapter = infiniteAdapter
            } else {
                ((holder.itemView.scrollView.adapter as InfiniteScrollAdapter).wrapped as AdapterWebcamsCarousel).webcams = item.webcams
                holder.itemView.scrollView.adapter.notifyDataSetChanged()
            }

            //Create selected onClickWebcamListener to save selected item position and display right name
            val itemSelectedListener = object : DiscreteScrollView.OnItemSelectedListener {
                override fun onItemSelectedChanged(position: Int) {
                    Timber.d("id section ${item.uid}")
                    //Need to be setted to came back to selected item after onRefresh
                    positionsAdapters.put(item.uid, position)

                    //Set right image name
                    val realPosition = (holder.itemView.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(position)
                    if (realPosition >= 0 && item.webcams.size > realPosition) {
                        val name = item.webcams[realPosition]?.title
                        holder.itemView.textViewNameWebcam.setText(name)
                    } else {
                        holder.itemView.textViewNameWebcam.setText("")
                    }
                }
            }

            //We need to store all selectedListeners to prevent them to be recycled
            selectedListeners.put(item.uid, itemSelectedListener)
            holder.itemView.scrollView.setItemSelectedListener(itemSelectedListener)

            holder.itemView.scrollView.post {
                if (item.isLoaded && item.isValid) {
                    val positionAdapter: Int
                    val existingPos = positionsAdapters.get(item.uid)
                    if (existingPos != null) {
                        positionAdapter = existingPos
                    } else {
                        positionAdapter = holder.itemView.scrollView.currentItem
                        positionsAdapters.put(item.uid, positionAdapter)
                    }

                    val realPos = (holder.itemView.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(positionAdapter)
                    if (realPos >= 0 && item.webcams.size > realPos) {
                        val name = item.webcams[realPos]?.title
                        holder.itemView.textViewNameWebcam.setText(name)
                    } else {
                        holder.itemView.textViewNameWebcam.setText("")
                    }
                    holder.itemView.scrollView.post {
                        holder.itemView.scrollView.layoutManager.scrollToPosition(positionAdapter)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            if (sectionFav != null) {
                return ITEM_FAV_SECTION
            } else {
                return ITEM_OTHER_SECTION
            }
        } else {
            return ITEM_OTHER_SECTION
        }
    }

    override fun getItemCount(): Int {
        return sections.size + if (sectionFav != null) 1 else 0
    }

    fun getPositionOfAllWebcams(): HashMap<Long, Int> {
        return positionsAdapters
    }

    fun setPositionOfAllWebcams(positionAdapters: HashMap<Long, Int>) {
        this.positionsAdapters = positionAdapters
    }

    class SectionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scrollView: DiscreteScrollView

        init {
            scrollView = view.scrollView
            scrollView.setItemTransformer(ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.9f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.CENTER)
                    .build())
            scrollView.setItemTransitionTimeMillis(400)
            scrollView.setSlideOnFling(true)
        }
    }

    class FavSectionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scrollView: DiscreteScrollView

        init {
            scrollView = view.scrollView
            scrollView.setItemTransformer(ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.9f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.CENTER)
                    .build())
            scrollView.setItemTransitionTimeMillis(400)
            scrollView.setSlideOnFling(true)
        }
    }
}