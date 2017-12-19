package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.github.salomonbrys.kodein.instance
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityListWebcam
import fr.openium.auvergnewebcams.activity.ActivitySearch
import fr.openium.auvergnewebcams.activity.ActivitySettings
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterCarousels
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.ext.isLollipopOrMore
import fr.openium.auvergnewebcams.ext.startActivity
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_carousel_webcam.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentCarouselWebcam : AbstractFragment() {

    companion object {
        private const val POSITION_LISTE = "POSITION_LISTE"
    }

    protected val api: AWApi by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.fragment_carousel_webcam

    private var position: Int = 0

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(POSITION_LISTE)
        }
        textViewSearch.setOnClickListener {
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
            if (applicationContext.hasNetwork) {
                oneTimeSubscriptions.add(api.getSections()
                        .subscribe({
                            Realm.getDefaultInstance().executeTransaction { realm ->
                                if (!it.isError && it.response()?.body() != null) {
                                    val sections = it.response()!!.body()!!
                                    for (section in sections.sections) {
                                        for (webcam in section.webcams) {
                                            if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                                // load media ld
                                                webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                                webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                            }

                                            val webcamDB = realm.where(Webcam::class.java)
                                                    .equalTo(Webcam::uid.name, webcam.uid)
                                                    .findFirst()
                                            if (webcamDB?.lastUpdate != null) {
                                                webcam.lastUpdate = webcamDB.lastUpdate
                                            }
                                        }
                                    }
                                    realm.insertOrUpdate(sections.sections)
                                }
                            }
                            activity?.runOnUiThread {
                                initAdapter()
                                swipeRefreshLayoutWebcams.isRefreshing = false
                            }
                        }, {
                            activity?.runOnUiThread {
                                swipeRefreshLayoutWebcams.isRefreshing = false
                            }
                        }))
            } else {
                activity?.runOnUiThread {
                    swipeRefreshLayoutWebcams.isRefreshing = false
                }
            }
        }

        initAdapter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (recyclerView?.layoutManager != null) {
            val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            outState.putInt(POSITION_LISTE, position)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_settings) {
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

    private fun initAdapter() {
        val sections = realm!!.where(Section::class.java)
                .sort(Section::order.name)
                .findAll()

        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = AdapterCarousels(context!!, { webcam, _ ->

                val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                    putExtra(Constants.KEY_ID, webcam.uid)
                    putExtra(Constants.KEY_TYPE, webcam.type)
                }
                val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
                startActivity(intent, bundle)
            }, sections, oneTimeSubscriptions, { section: Section ->
                startActivity<ActivityListWebcam>(ActivityListWebcam.getBundle(section.uid))
            })
            recyclerView.scrollToPosition(position)
        } else {
            (recyclerView.adapter as AdapterCarousels).items = sections
            recyclerView.adapter.notifyDataSetChanged()
        }
    }


}
