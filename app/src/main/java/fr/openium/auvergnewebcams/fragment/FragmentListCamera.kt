package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcam
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.adapter.ItemWebcam
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list_camera.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_list_camera

    protected val mItems = ArrayList<ItemWebcam>()
    protected val picasso: Picasso by kodeinInjector.instance()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sections = realm!!.where(Section::class.java)
                .findAllSorted(Section::order.name, Sort.ASCENDING)
        for (section in sections) {
            for (webcam in section.webcams) {
                val nameSection = if (section.title == null) "" else section.title!!
                val imageSection = if (section.imageName == null) "" else section.imageName!!
                val item = ItemWebcam(webcam, nameSection, imageSection, section.webcams.size)
                mItems.add(item)
            }
        }
        initAdapter()
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AdapterWebcam(context, picasso, {
            webcam, _ ->
            val intent = Intent(context, ActivityWebcam::class.java).apply { putExtra(Constants.KEY_ID, webcam.uid) }
            startActivity(intent)
        }, mItems)
    }


}
