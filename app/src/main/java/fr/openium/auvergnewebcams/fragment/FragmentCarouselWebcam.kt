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
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.applicationContext
import fr.openium.kotlintools.ext.isLollipopOrMore
import fr.openium.kotlintools.ext.startActivity
import fr.openium.kotlintools.ext.toUnixTimestamp
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.realm.RealmList
import io.realm.RealmResults
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

            val map = listKeyPos.zip(listValuePos.toList()).toMap()
            val hashMap = HashMap<Long, Int>()
            hashMap.putAll(map)
            positionAdapters = hashMap
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

        initAdapter()
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

            val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext!!, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
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
        val delay = PreferencesAW.getWebcamsDelayRefreshValue(applicationContext!!)
        if (PreferencesAW.isWebcamsDelayRefreshActive(applicationContext!!)) {
            oneTimeDisposables.add(Observable.timer(delay.toLong(), TimeUnit.MINUTES)
                    .fromIOToMain()
                    .subscribe {
                        PreferencesAW.setLastUpdateTimestamp(applicationContext!!, System.currentTimeMillis().toUnixTimestamp())
                        oneTimeDisposables.add(apiHelper.getSections().fromIOToMain().subscribe({
                            removeGlideCache()
                        }, { }))
                        startDelayRefreshWebcams()
                    })
        } else {
            PreferencesAW.setLastUpdateTimestamp(applicationContext!!, System.currentTimeMillis().toUnixTimestamp())
        }
    }

    private fun startDelayRefreshWeather() {
        val delay = PreferencesAW.getWeatherDelayRefreshValue()
        oneTimeDisposables.add(Observable.timer(delay.toLong(), TimeUnit.SECONDS)
                .fromIOToMain()
                .subscribe {
                    if (isAlive) {
                        PreferencesAW.setLastUpdateWeatherTimestamp(applicationContext!!, System.currentTimeMillis().toUnixTimestamp())
                        apiHelper.getWeatherForAllSections(applicationContext!!)
                        startDelayRefreshWeather()
                    }
                })
    }

    // --- Init adapters ---

    private fun initAdapter() {
        if (isAlive) {
            Timber.d("[INFO] UPDATE Webcams")
            val sectionsObs = realm!!.where(Section::class.java)
                    .sort(Section::order.name)
                    .isNotEmpty(Section::webcams.name)
                    .findAll().asFlowable()

            val webcamsObs = realm!!.where(Webcam::class.java)
                    .findAll().asFlowable()

            realm?.executeTransaction {
                it.where(Section::class.java)
                        .equalTo(Section::uid.name, Constants.FAVORI_SECTION_ID)
                        .findFirst()?.deleteFromRealm()
            }

            oneTimeDisposables.add(Flowable.combineLatest(sectionsObs, webcamsObs, BiFunction { sections: RealmResults<Section>, webcams: RealmResults<Webcam> ->
                sections
            }).subscribe({ sections ->
                val webcamsFavoris = realm?.where(Webcam::class.java)
                        ?.sort(Webcam::title.name)
                        ?.equalTo(Webcam::isFavoris.name, true)
                        ?.findAll()

                val webcamsFav = RealmList<Webcam>()
                val sectionFavoris: Section?
                if (webcamsFavoris != null && webcamsFavoris.isNotEmpty()) {
                    sectionFavoris = Section(uid = Constants.FAVORI_SECTION_ID, order = -1, title = getString(R.string.favoris_section_title), imageName = "star", webcams = webcamsFav)
                    webcamsFav.addAll(webcamsFavoris.toList())
                } else {
                    sectionFavoris = null
                }

                val weatherList = realm?.where(Weather::class.java)?.findAll()

                if (recyclerView.adapter == null) {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = AdapterCarousels({ webcam, _ ->
                        startActivityDetailCamera(webcam)
                    }, sections, sectionFavoris, onClickSectionListener = { section ->
                        startActivityListWebcam(section)
                    }, weatherList = weatherList)

                    positionAdapters?.let {
                        (recyclerView.adapter as AdapterCarousels).setPositionOfAllWebcams(it)
                    }

                    recyclerView.scrollToPosition(actualPositionOfTheList)
                } else {
                    (recyclerView.adapter as AdapterCarousels).sectionFav = sectionFavoris
                    (recyclerView.adapter as AdapterCarousels).weatherList = weatherList
                    recyclerView.adapter.notifyDataSetChanged()
                }
            }, {
                Timber.e("Error init adapter ${it.message}")
            }))
        }
    }

// --- Refresh method ---

    private fun refreshMethod() {
        oneTimeDisposables.add(Single.zip(Observable.timer(2, TimeUnit.SECONDS).singleOrError(), apiHelper.getSections(), BiFunction { time: Long, list: Result<SectionList> ->

        }).observeOn(AndroidSchedulers.mainThread()).subscribe({
            removeGlideCache()
            swipeRefreshLayoutWebcams.isRefreshing = false
        }, { result ->
            swipeRefreshLayoutWebcams.isRefreshing = false
        }))
    }

    private fun removeGlideCache() {
        oneTimeDisposables.add(Observable
                .fromCallable {
                    Glide.get(applicationContext!!).clearDiskCache()
                }
                .fromIOToMain()
                .subscribe {
                    Glide.get(applicationContext!!).clearMemory()
                    PreferencesAW.setLastUpdateTimestamp(applicationContext!!, System.currentTimeMillis().toUnixTimestamp())
                })
    }

// --- Go to another activity methods

    private fun startActivityDetailCamera(webcam: Webcam) {
        //Analytics
        AnalyticsUtils.selectWebcamDetails(context!!, webcam.title ?: "")

        val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
            putExtra(Constants.KEY_ID, webcam.uid)
            putExtra(Constants.KEY_TYPE, webcam.type)
        }
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext!!, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
        startActivity(intent, bundle)
    }

    private fun startActivityListWebcam(section: Section) {
        //Analytics
        AnalyticsUtils.selectSectionDetails(context!!, section.title ?: "")

        startActivity<ActivityListWebcam>(ActivityListWebcam.getBundle(section.uid))
    }

// --- Analytics ---

    private fun sendAllAnalyticsData() {
        //Analytics
        AnalyticsUtils.appIsOpen(context!!)
        AnalyticsUtils.sendAllUserPreferences(context!!)
    }
}