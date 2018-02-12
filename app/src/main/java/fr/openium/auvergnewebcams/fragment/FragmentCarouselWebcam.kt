package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.AWWeatherApi
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_carousel_webcam.*
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

    override val layoutId: Int
        get() = R.layout.fragment_carousel_webcam

    private var position: Int = 0
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
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(POSITION_LISTE)

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
            position = 0

            //Analytics
            AnalyticsUtils.buttonHomeRefreshed(context!!)

            manageRefreshWebcams()
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
        startDelayRefreshWebcams()
        startDelayRefreshWeather()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (recyclerView?.layoutManager != null) {
            val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            outState.putInt(POSITION_LISTE, position)

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

    private fun manageRefreshWebcams() {
        if (applicationContext.hasNetwork) {
            oneTimeSubscriptions.add(api.getSections()
                    .subscribe({ result ->
                        Realm.getDefaultInstance().use {
                            it.executeTransaction {
                                if (!result.isError && result.response()?.body() != null) {
                                    val sections = result.response()!!.body()!!

                                    for (section in sections.sections) {

                                        section.latitude = Math.round(section.latitude * 100.0) / 100.0
                                        section.longitude = Math.round(section.longitude * 100.0) / 100.0

                                        for (webcam in section.webcams) {
                                            if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                                // load media ld
                                                webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                                webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                            }

                                            val webcamDB = it.where(Webcam::class.java)
                                                    .equalTo(Webcam::uid.name, webcam.uid)
                                                    .findFirst()
                                            if (webcamDB != null) {
                                                if (webcamDB.lastUpdate != null) {
                                                    webcam.lastUpdate = webcamDB.lastUpdate
                                                }
                                                webcam.isFavoris = webcamDB.isFavoris
                                            }
                                            if (webcam.hidden == null) {
                                                webcam.hidden = false
                                            }
                                        }

                                        section.webcams = RealmList<Webcam>().apply {
                                            addAll(section.webcams.filter { it.hidden == false })
                                        }
                                    }

                                    it.where(Section::class.java).findAll().deleteAllFromRealm()
                                    it.where(Webcam::class.java).findAll().deleteAllFromRealm()

                                    it.insertOrUpdate(sections.sections)
                                }
                            }
                            activity?.runOnUiThread {
                                removeGlideCache()
                            }
                        }
                    }, {
                        activity?.runOnUiThread {
                            swipeRefreshLayoutWebcams?.isRefreshing = false
                        }
                    }))
        } else {
            activity?.runOnUiThread {
                swipeRefreshLayoutWebcams?.isRefreshing = false
            }
        }
    }

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

    private fun removeGlideCache() {
        oneTimeSubscriptions.add(Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    Glide.get(applicationContext).clearDiskCache()
                    activity?.runOnUiThread {
                        Glide.get(applicationContext).clearMemory()
//                        if (PreferencesAW.isWebcamsDelayRefreshActive(applicationContext)) {
                        PreferencesAW.setLastUpdateTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())
//                        }
                        initAdapter(PreferencesAW.getLastUpdateWebcamsTimestamp(applicationContext))
                        swipeRefreshLayoutWebcams?.isRefreshing = false
                    }
                })
    }

    private fun initAdapter(lastUpdate: Long) {
        if (isAlive) {
            Timber.d("[INFO] UPDATE Webcams")

            val sectionFavoris = Section(uid = -1, order = -1, title = getString(R.string.favoris_section_title), imageName = "star")

            val webcamsFavoris = realm!!.where(Webcam::class.java)
                    .sort(Webcam::title.name)
                    .equalTo(Webcam::isFavoris.name, true)
                    .findAll()

            if (!webcamsFavoris.isEmpty()) {
                sectionFavoris.webcams.addAll(webcamsFavoris)
            }

            val sections = realm!!.where(Section::class.java)
                    .sort(Section::order.name)
                    .isNotEmpty(Section::webcams.name)
                    .findAll()

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = AdapterCarousels(context!!, { webcam, _ ->
                    //Analytics
                    AnalyticsUtils.selectWebcamDetails(context!!, webcam.title!!)

                    startActivityDetailCamera(webcam)
                }, sections, composites = oneTimeSubscriptions, sectionFavoris = sectionFavoris, listenerSectionClick = { section ->
                    //Analytics
                    AnalyticsUtils.selectSectionDetails(context!!, section.title!!)

                    startActivity<ActivityListWebcam>(ActivityListWebcam.getBundle(section.uid))
                }, lastUpdate = lastUpdate, oneTimeSubscriptions = oneTimeSubscriptions, realm = realm!!)

                //Optimization:
                recyclerView.setHasFixedSize(true)
                recyclerView.setItemViewCacheSize(20)
                recyclerView.isDrawingCacheEnabled = true
                recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

                if (positionAdapters != null) {
                    (recyclerView.adapter as AdapterCarousels).setPositionOfAllWebcams(positionAdapters!!)
                }

                recyclerView.scrollToPosition(position)
            } else {
                (recyclerView.adapter as AdapterCarousels).items = sections
                (recyclerView.adapter as AdapterCarousels).sectionFavoris = sectionFavoris
                (recyclerView.adapter as AdapterCarousels).lastUpdate = lastUpdate
                recyclerView.invalidate()
                recyclerView.adapter.notifyDataSetChanged()
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

    private fun startActivityDetailCamera(webcam: Webcam) {
        val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
            putExtra(Constants.KEY_ID, webcam.uid)
            putExtra(Constants.KEY_TYPE, webcam.type)
        }
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
        startActivity(intent, bundle)
    }

    private fun sendAllAnalyticsData() {
        //Analytics
        AnalyticsUtils.appIsOpen(context!!)
        AnalyticsUtils.sendAllUserPreferences(context!!)
    }

}