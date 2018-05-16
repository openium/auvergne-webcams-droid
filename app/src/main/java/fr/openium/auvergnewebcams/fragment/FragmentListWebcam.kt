package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterWebcams
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.kotlintools.ext.applicationContext
import fr.openium.rxtools.ext.fromIOToMain
import kotlinx.android.synthetic.main.fragment_list_webcam.*
import timber.log.Timber
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSection()
        oneTimeSubscriptions.add(Events.eventCameraFavoris.obs
                .fromIOToMain()
                .subscribe({
                    if (isAlive && section.uid == -1L) {
                        initSection()
                    }
                }, { error ->
                    Timber.e(error)
                }))
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initSection() {
        val uidSection = arguments?.getLong(Constants.ARG_SECTION_UID) ?: -1L
        val realmSection = realm!!.where(Section::class.java)
                .equalTo(Section::uid.name, uidSection)
                .findFirst()
        if (realmSection == null) {
            section = Section(title = getString(R.string.favoris_section_title), imageName = "star")
            section.webcams.addAll(realm!!.where(Webcam::class.java)
                    .equalTo(Webcam::isFavoris.name, true)
                    .findAll())
        } else {
            section = realmSection
        }
        webcams.clear()
        webcams.addAll(section.webcams)
        initSectionInfo()
    }

    private fun initSectionInfo() {
        (activity as AppCompatActivity).supportActionBar?.title = section.title
        initAdapter()
    }

    private fun initAdapter() {
        if (webcams.isEmpty()) {
            activity?.finish()
        } else {
            val webcamsList = ArrayList<Webcam>()
            webcamsList.addAll(webcams)
            if (recyclerView.adapter == null) {
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.adapter = AdapterWebcams(applicationContext!!, webcamsList, { webcam ->
                    val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                        putExtra(Constants.KEY_ID, webcam.uid)
                        putExtra(Constants.KEY_TYPE, webcam.type)
                    }
                    val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext!!, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
                    startActivity(intent, bundle)
                }, oneTimeSubscriptions, section, realm!!)
            } else {
                (recyclerView.adapter as AdapterWebcams).items = webcamsList
                (recyclerView.adapter as AdapterWebcams).headerSection = section
                recyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_list_webcam

}