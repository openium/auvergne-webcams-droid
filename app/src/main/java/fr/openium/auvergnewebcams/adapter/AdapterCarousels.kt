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
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.auvergnewebcams.utils.WeatherUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.realm.Realm
import kotlinx.android.synthetic.main.header_list_webcam.view.*
import kotlinx.android.synthetic.main.item_carousel.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by laura on 23/03/2017.
 */
class AdapterCarousels(val context: Context,
                       val listener: ((Webcam, Int) -> Unit)? = null,
                       var items: List<Section>,
                       val composites: CompositeDisposable,
                       var sectionFavoris: Section,
                       val listenerSectionClick: ((Section) -> Unit),
                       var lastUpdate: Long,
                       var oneTimeSubscriptions: CompositeDisposable,
                       var realm: Realm) : RecyclerView.Adapter<AdapterCarousels.WebcamHolder>() {

    private val heightImage: Int
    private var positionsAdapters = HashMap<Long, Int>()
    private var listeners = LongSparseArray<DiscreteScrollView.OnItemSelectedListener>()
    private var subs = HashMap<Long, Disposable>()

    init {
        heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return WebcamHolder(view)
    }

    override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
        var item: Section?
        var isFav = false
        if (position == 0) {
            item = sectionFavoris
            isFav = true
        } else {
            item = items.get(position - 1)
        }
        val uid = item.uid

        if (item.webcams.isEmpty()) {
            holder.mTextViewNameSection.gone()
            holder.mTextViewNameWebcam.gone()
            holder.mTextViewNbCameras.gone()
            holder.mTextViewNbCamerasArrow.gone()
            holder.mImageViewSection.gone()
            holder.mLinearLayoutSection.gone()
            holder.scrollView.gone()
            holder.mImageViewSectionWeather.gone()
            holder.mTextViewSectionWeather.gone()
        } else {
            holder.mTextViewNameSection.show()
            holder.mTextViewNameWebcam.show()
            holder.mTextViewNbCameras.show()
            holder.mTextViewNbCamerasArrow.show()
            holder.mImageViewSection.show()
            holder.mLinearLayoutSection.show()
            holder.scrollView.show()
            if (!isFav) {
                holder.mImageViewSectionWeather.show()
                holder.mTextViewSectionWeather.show()
            } else {
                holder.mImageViewSectionWeather.gone()
                holder.mTextViewSectionWeather.gone()
            }

            val section = item.title

            holder.mLinearLayoutSection.setOnClickListener {
                listenerSectionClick.invoke(item!!)
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

            //Weather
            val weather = realm.where(Weather::class.java).equalTo(Weather::lat.name, item.latitude).equalTo(Weather::lon.name, item.longitude).findFirst()
            if (weather != null && PreferencesAW.getIfWeatherCouldBeDisplayed(context)) {
                //Set weather
                holder.itemView?.imageViewSectionWeather?.setImageResource(WeatherUtils.weatherImage(weather.id!!))
                holder.itemView?.textViewSectionWeather?.setText(context.getString(R.string.weather_celcius, WeatherUtils.convertKelvinToCelcius(weather.temp!!)))
            }

            holder.itemView.setOnClickListener {
                val pos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(holder.scrollView.currentItem)
                val webcam = item!!.webcams.get(pos)
                if (webcam != null) {
                    listener?.invoke(webcam, position)
                }
            }

            val adapter = AdapterWebcamsCarousel(context, listener, item, composites, lastUpdate)
            val infiniteAdapter = InfiniteScrollAdapter.wrap(adapter)
            holder.scrollView.adapter = infiniteAdapter

            val listener: DiscreteScrollView.OnItemSelectedListener
            listener = object : DiscreteScrollView.OnItemSelectedListener {
                override fun onItemSelectedChanged(position: Int) {
//                        Timber.d("addItemSelectedListener ${item.uid}  newPosition: $position")
                    Timber.d("Real posAdapter put in listener $position")
                    positionsAdapters.put(item!!.uid, position)

                    val realPosition = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(position)
                    if (realPosition >= 0 && item!!.webcams.size > realPosition) {
                        val name = item!!.webcams[realPosition]!!.title
                        holder.mTextViewNameWebcam.setText(name)
                    } else {
                        holder.mTextViewNameWebcam.setText("")
                    }
                }
            }
            listeners.put(item.uid, listener)
            holder.scrollView.setItemSelectedListener(listener)

            holder.scrollView.post {
                if (!item!!.isValid) {
                    Realm.getDefaultInstance().use {
                        it.executeTransaction {
                            item = it.where(Section::class.java).equalTo(Section::uid.name, uid).findFirst()
                        }
                    }
                }
                if (item != null) {
                    val positionAdapter: Int
                    val existingPos = positionsAdapters.get(item!!.uid)
                    if (existingPos != null) {
                        positionAdapter = existingPos
                    } else {
                        positionAdapter = holder.scrollView.currentItem
                        //Timber.d("Real posAdapter put in post $positionAdapter")
                        positionsAdapters.put(item!!.uid, positionAdapter)
                    }
                    //Timber.d("Real posAdapter get $positionAdapter")

                    val realPos = (holder.scrollView.adapter as InfiniteScrollAdapter).getRealPosition(positionAdapter)
                    //Timber.d("Real pos get $realPos")
                    if (realPos >= 0 && item!!.webcams.size > realPos) {
                        val name = item!!.webcams[realPos]!!.title
                        holder.mTextViewNameWebcam.setText(name)
                    } else {
                        holder.mTextViewNameWebcam.setText("")
                    }
                    holder.scrollView.post {
                        //Timber.d("Real posAdapter get $positionAdapter")
                        holder.scrollView.layoutManager.scrollToPosition(positionAdapter)
                    }
                }
            }

            if (item!!.latitude != 0.0 && item!!.longitude != 0.0) {
                if (subs.get(item!!.uid) == null) {
                    subs.put(item!!.uid, realm.where(Weather::class.java).equalTo(Weather::lat.name, item!!.latitude).equalTo(Weather::lon.name, item!!.longitude).findAll().asChangesetObservable().subscribe({
                        if (it.collection.isNotEmpty()) {
                            val newWeather = it.collection.first()

                            if (newWeather != null && PreferencesAW.getIfWeatherCouldBeDisplayed(context)) {
                                //Set weather
                                holder.itemView?.imageViewSectionWeather?.setImageResource(WeatherUtils.weatherImage(newWeather.id!!))
                                holder.itemView?.textViewSectionWeather?.setText(context.getString(R.string.weather_celcius, WeatherUtils.convertKelvinToCelcius(newWeather.temp!!)))
                            }
                        }
                    }, { e ->
                        Timber.e("ERROR ${e.message}")
                    }))
                    oneTimeSubscriptions.add(subs.get(item!!.uid)!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    fun getPositionOfAllWebcams(): HashMap<Long, Int> {
        return positionsAdapters
    }

    fun setPositionOfAllWebcams(positionAdapters: HashMap<Long, Int>) {
        this.positionsAdapters = positionAdapters
    }

    class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTextViewNameSection: TextView
        val mTextViewNameWebcam: TextView
        val mTextViewNbCameras: TextView
        val mTextViewNbCamerasArrow: TextView
        val mImageViewSection: ImageView
        val mLinearLayoutSection: LinearLayout
        val scrollView: DiscreteScrollView
        val mImageViewSectionWeather: ImageView
        val mTextViewSectionWeather: TextView

        init {
            mTextViewNameSection = view.textViewNameSection
            mTextViewNameWebcam = view.textViewNameWebcam
            mTextViewNbCameras = view.textViewNbCameras
            mTextViewNbCamerasArrow = view.textViewNbCamerasArrow
            mImageViewSection = view.imageViewSection
            mLinearLayoutSection = view.linearLayoutSection
            scrollView = view.scrollView
            scrollView.setItemTransformer(ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.9f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.CENTER)
                    .build())
            scrollView.setItemTransitionTimeMillis(400)
            scrollView.setSlideOnFling(true)
            mImageViewSectionWeather = view.imageViewSectionWeather
            mTextViewSectionWeather = view.textViewSectionWeather
        }
    }
}