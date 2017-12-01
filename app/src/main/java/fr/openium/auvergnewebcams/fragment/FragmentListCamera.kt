package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.salomonbrys.kodein.instance
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcam
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.hasNetwork
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

    protected val api: AWApi by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.fragment_list_camera

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefreshLayoutWebcams.setOnRefreshListener {
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
                startActivity(intent)
            }, sections)
        } else {
            (recyclerView.adapter as AdapterWebcam).items = sections
            recyclerView.adapter.notifyDataSetChanged()
        }
    }


}
