package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcam
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.adapter.ItemWebcam
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list_camera.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_list_camera

    protected val mItems = ArrayList<ItemWebcam>()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sections = realm!!.where(Section::class.java)
                .findAllSorted(Section::order.name, Sort.ASCENDING)
        initAdapter(sections)
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initAdapter(sections: RealmResults<Section>) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AdapterWebcam(context!!, { webcam, _ ->

            val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                putExtra(Constants.KEY_ID, webcam.uid)
                putExtra(Constants.KEY_TYPE, webcam.type)
            }
            startActivity(intent)
        }, sections)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        recyclerView.invalidateItemDecorations()
    }


}
