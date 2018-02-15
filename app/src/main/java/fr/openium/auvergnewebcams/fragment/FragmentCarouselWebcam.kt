package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.github.salomonbrys.kodein.instance
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityListWebcam
import fr.openium.auvergnewebcams.activity.ActivitySearch
import fr.openium.auvergnewebcams.activity.ActivitySettings
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterCarousels
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.ext.*
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.AWWeatherApi
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_carousel_webcam.*
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentCarouselWebcam : AbstractFragment() {

    companion object {
        private const val POSITION_LISTE = "POSITION_LISTE"
        private const val POSITION_ADAPTER_KEYS_LISTE = "POSITION_ADAPTER_KEYS_LISTE"
        private const val POSITION_ADAPTER_VALUES_LISTE = "POSITION_ADAPTER_VALUES_LISTE"
    }

    protected val api: AWApi by kodeinInjector.instance()
    protected val apiWeather: AWWeatherApi by kodeinInjector.instance()
    protected val apiHelper: ApiHelper by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.fragment_carousel_webcam

    private var actualPositionOfTheList: Int = 0
    private var positionAdapters: HashMap<Long, Int>? = null

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendAllAnalyticsData()

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Get saved position
        if (savedInstanceState != null) {
            actualPositionOfTheList = savedInstanceState.getInt(POSITION_LISTE)

            val listKeyPos = savedInstanceState.getLongArray(POSITION_ADAPTER_KEYS_LISTE)
            val listValuePos = savedInstanceState.getIntArray(POSITION_ADAPTER_VALUES_LISTE)

            positionAdapters = (listKeyPos.zip(listValuePos.toList()).toMap() as HashMap<Long, Int>)
        }

        textViewSearch.setOnClickListener {
            //Analytics
            AnalyticsUtils.buttonSearchClicked(context!!)

            val intent = Intent(applicationContext, ActivitySearch::class.java)
            if (activity?.isLollipopOrMore() == true) {
                val transitionName = getString(R.string.transition_search_name)
                val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, textViewSearch, transitionName)

                startActivity(intent, activityOptions.toBundle())
            } else {
                val intentSearch = Intent(applicationContext, ActivitySearch::class.java)
                startActivity(intentSearch)
            }
        }

        swipeRefreshLayoutWebcams.setOnRefreshListener {
            actualPositionOfTheList = 0

            //Analytics
            AnalyticsUtils.buttonHomeRefreshed(context!!)

            refreshMethod()
        }

        oneTimeSubscriptions.add(Events.eventCameraFavoris.obs
                .fromIOToMain()
                .subscribe {
                    initAdapter(PreferencesAW.getLastUpdateWebcamsTimestamp(applicationContext))
                })

        initAdapter(PreferencesAW.getLastUpdateWebcamsTimestamp(applicationContext))
    }

    override fun onStart() {
        super.onStart()

        //Start the 2 timers when the fragment start
        startDelayRefreshWebcams()
        startDelayRefreshWeather()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (recyclerView?.layoutManager != null) {
            //Keep position of the vertical list
            val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            outState.putInt(POSITION_LISTE, position)

            //Keep position of the horizontals list
            val posOfHorizontalScrollView = (recyclerView.adapter as AdapterCarousels).getPositionOfAllWebcams()
            outState.putLongArray(POSITION_ADAPTER_KEYS_LISTE, posOfHorizontalScrollView.keys.toLongArray())
            outState.putIntArray(POSITION_ADAPTER_VALUES_LISTE, posOfHorizontalScrollView.values.toIntArray())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_settings) {
            //Analytics
            AnalyticsUtils.buttonSettingsClicked(context!!)

            val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
            startActivity(Intent(applicationContext, ActivitySettings::class.java), bundle)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    // --- Start delay to refresh data ---

    private fun startDelayRefreshWebcams() {
        val delay = PreferencesAW.getWebcamsDelayRefreshValue(applicationContext)
        if (PreferencesAW.isWebcamsDelayRefreshActive(applicationContext)) {
            oneTimeSubscriptions.add(Observable.timer(delay.toLong(), TimeUnit.MINUTES)
                    .fromIOToMain()
                    .subscribe {
                        PreferencesAW.setLastUpdateTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())
                        initAdapter(PreferencesAW.getLastUpdateWebcamsTimestamp(applicationContext))
                        startDelayRefreshWebcams()
                    })
        } else {
            PreferencesAW.setLastUpdateTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())
        }
    }

    private fun startDelayRefreshWeather() {
        val delay = PreferencesAW.getWeatherDelayRefreshValue()
        oneTimeSubscriptions.add(Observable.timer(delay.toLong(), TimeUnit.SECONDS)
                .fromIOToMain()
                .subscribe {
                    Timber.d("UPDATE")
                    PreferencesAW.setLastUpdateWeatherTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())
                    initWeather()
                    startDelayRefreshWeather()
                })
    }

    // --- Init adapters ---

    private fun initAdapter(lastUpdate: Long) {
        if (isAlive) {
            Timber.d("[INFO] UPDATE Webcams")

            var sectionFavorisDB = realm!!.where(Section::class.java).equalTo(Section::uid.name, Constants.FAVORI_SECTION_ID).findFirst()
            if (sectionFavorisDB == null) {
                realm!!.executeTransaction {
                    val sectionFavoris = Section(uid = Constants.FAVORI_SECTION_ID, order = -1, title = getString(R.string.favoris_section_title), imageName = "star")
                    it.insertOrUpdate(sectionFavoris)
                }
            }

            sectionFavorisDB = realm!!.where(Section::class.java).equalTo(Section::uid.name, Constants.FAVORI_SECTION_ID).findFirst()

            val webcamsFavoris = realm!!.where(Webcam::class.java)
                    .sort(Webcam::title.name)
                    .equalTo(Webcam::isFavoris.name, true)
                    .findAll()

            realm!!.executeTransaction {
                sectionFavorisDB!!.webcams.clear()
                sectionFavorisDB.webcams.addAll(webcamsFavoris)
            }

            val sections = realm!!.where(Section::class.java)
                    .sort(Section::order.name)
                    .isNotEmpty(Section::webcams.name)
                    .findAll()

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = AdapterCarousels(context!!, { webcam, _ ->
                    startActivityDetailCamera(webcam)
                }, sections, composites = oneTimeSubscriptions, listenerSectionClick = { section ->
                    startActivityListWebcam(section)
                }, lastUpdate = lastUpdate)

                if (positionAdapters != null) {
                    (recyclerView.adapter as AdapterCarousels).setPositionOfAllWebcams(positionAdapters!!)
                }

                recyclerView.setHasFixedSize(true)

                recyclerView.scrollToPosition(actualPositionOfTheList)
            } else {
                recyclerView.post {
                    (recyclerView.adapter as AdapterCarousels).items = sections
                    (recyclerView.adapter as AdapterCarousels).lastUpdate = lastUpdate
                    recyclerView.adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initWeather() {
        if (isAlive) {
            Timber.d("[INFO] UPDATE Weather")

            val sections = realm!!.where(Section::class.java)
                    .sort(Section::order.name)
                    .isNotEmpty(Section::webcams.name)
                    .findAll()

            for (i in 0..sections.size - 1) {
                if (sections.get(i)!!.latitude != 0.0 || sections.get(i)!!.longitude != 0.0) {
//                Timber.d("Old lat = ${sections.get(i).latitude} | Old lon = ${sections.get(i).longitude}")
                    oneTimeSubscriptions.add(apiWeather.queryByGeographicCoordinates(sections.get(i)!!.latitude, sections.get(i)!!.longitude, getString(R.string.app_weather_id)).fromIOToMain().subscribe({ weatherRest ->
                        Realm.getDefaultInstance().use {
                            if (weatherRest.response() != null) {
                                it.executeTransaction {
                                    it.where(Weather::class.java).equalTo(Weather::lat.name, weatherRest.response()!!.body()!!.coord!!.lat).equalTo(Weather::lon.name, weatherRest.response()!!.body()!!.coord!!.lon).findAll().deleteAllFromRealm()

                                    val weather = Weather(weatherRest.response()!!.body()?.weather!!.get(0).id!!, weatherRest.response()!!.body()?.main!!.temp!!, weatherRest.response()!!.body()!!.coord!!.lon!!, weatherRest.response()!!.body()!!.coord!!.lat!!)
                                    it.insertOrUpdate(weather)
                                }
                            }
                        }
                    }, { e ->
                        Timber.e("Error init weather ${e.message}")
                    }))
                }
            }
        }
    }

    // --- Refresh method ---

    private fun refreshMethod() {
        oneTimeSubscriptions.add(Single.zip(Observable.timer(2, TimeUnit.SECONDS).toSingle(), apiHelper.getSections(), BiFunction { time: Observable<Long>, list: Result<SectionList> ->

        }).observeOn(AndroidSchedulers.mainThread()).subscribe({
            removeGlideCache()
            swipeRefreshLayoutWebcams.isRefreshing = false
        }, { result ->
            swipeRefreshLayoutWebcams.isRefreshing = false
        }))
    }

    private fun removeGlideCache() {
        oneTimeSubscriptions.add(Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    Glide.get(applicationContext).clearDiskCache()
                    activity?.runOnUiThread {
                        Glide.get(applicationContext).clearMemory()
                        initAdapter(PreferencesAW.getLastUpdateWebcamsTimestamp(context!!))
                        PreferencesAW.setLastUpdateTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())
                    }
                })
    }

    // --- Go to another activity methods

    private fun startActivityDetailCamera(webcam: Webcam) {
        //Analytics
        AnalyticsUtils.selectWebcamDetails(context!!, webcam.title!!)

        val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
            putExtra(Constants.KEY_ID, webcam.uid)
            putExtra(Constants.KEY_TYPE, webcam.type)
        }
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
        startActivity(intent, bundle)
    }

    private fun startActivityListWebcam(section: Section) {
        //Analytics
        AnalyticsUtils.selectSectionDetails(context!!, section.title!!)

        startActivity<ActivityListWebcam>(ActivityListWebcam.getBundle(section.uid))
    }

    // --- Analytics ---

    private fun sendAllAnalyticsData() {
        //Analytics
        AnalyticsUtils.appIsOpen(context!!)
        AnalyticsUtils.sendAllUserPreferences(context!!)
    }

}