package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcams
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import kotlinx.android.synthetic.main.fragment_list_webcam.*
import java.util.*

/**
 * Created by nicolas on 19/12/2017.
 */
class FragmentListWebcam : AbstractFragment() {

    private val webcams = ArrayList<Webcam>()

    lateinit var section: Section
    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSection()
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initSection() {
        val uidSection = arguments?.getLong(Constants.ARG_SECTION_UID)
        val realmSection = realm!!.where(Section::class.java)
                .equalTo(Section::uid.name, uidSection)
                .findFirst()
        if (realmSection == null) {
            section = Section(title = getString(R.string.favoris_section_title), imageName = "pdd_landscape")
            section.webcams.addAll(realm!!.where(Webcam::class.java)
                    .equalTo(Webcam::isFavoris.name, true)
                    .findAll())
        } else {
            section = realmSection
        }
        webcams.addAll(section.webcams)
        initSectionInfo()
    }

    private fun initSectionInfo() {
        (activity as AppCompatActivity).supportActionBar?.title = section.title
        initAdapter()
    }

    private fun initAdapter() {
        val webcamsAdapter = ArrayList<Webcam>()
        webcamsAdapter.addAll(webcams)
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.adapter = AdapterWebcams(applicationContext, webcamsAdapter, { webcam ->
                val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                    putExtra(Constants.KEY_ID, webcam.uid)
                    putExtra(Constants.KEY_TYPE, webcam.type)
                }
                val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
                startActivity(intent, bundle)
            }, oneTimeSubscriptions, section)
        } else {
            (recyclerView.adapter as AdapterWebcams).items = webcamsAdapter
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_list_webcam

}