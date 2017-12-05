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
import fr.openium.auvergnewebcams.activity.ActivitySearch
import fr.openium.auvergnewebcams.activity.ActivitySettings
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcam
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.ext.isLollipopOrMore
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list_camera.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment() {

    companion object {
        private const val POSITION_LISTE = "POSITION_LISTE"
    }

    protected val api: AWApi by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.fragment_list_camera

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
                            if (!it.isError && it.response()?.body() != null) {
                                val sections = it.response()!!.body()!!
                                for (section in sections.sections) {
                                    for (webcam in section.webcams) {
                                        if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                            // load media ld
                                            webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                            webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                        }
                                    }
                                }
                                Realm.getDefaultInstance().use {
                                    it.executeTransaction {
                                        it.insertOrUpdate(sections.sections)
                                    }
                                }
                            }
                            activity?.runOnUiThread {
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


        oneTimeSubscriptions.add(realm!!.where(Section::class.java)
                .findAllSortedAsync(Section::order.name, Sort.ASCENDING)
                .asFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { sections ->
                    initAdapter(sections)
                })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        outState.putInt(POSITION_LISTE, position)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_settings) {
            val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle()
            startActivity(Intent(applicationContext, ActivitySettings::class.java), bundle)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initAdapter(sections: RealmResults<Section>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = AdapterWebcam(context!!, { webcam, _ ->

                val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                    putExtra(Constants.KEY_ID, webcam.uid)
                    putExtra(Constants.KEY_TYPE, webcam.type)
                }
                val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle()
                startActivity(intent, bundle)
            }, sections)
        } else {
            (recyclerView.adapter as AdapterWebcam).items = sections
            recyclerView.adapter.notifyDataSetChanged()
        }
        recyclerView.scrollToPosition(position)
    }


}
