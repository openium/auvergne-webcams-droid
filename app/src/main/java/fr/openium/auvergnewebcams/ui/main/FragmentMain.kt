package fr.openium.auvergnewebcams.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.settings.ActivitySettings
import fr.openium.auvergnewebcams.ui.webcamdetail.ActivityWebcam
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.startActivity
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber


class FragmentMain : AbstractFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_main

    private lateinit var viewModelMain: ViewModelMain

    private var adapter: AdapterSections? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        AnalyticsUtils.appIsOpen(requireContext())
        AnalyticsUtils.sendAllUserProperties(requireContext())

        viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setListeners()
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_settings) {
            AnalyticsUtils.settingsClicked(requireContext())
            startActivity<ActivitySettings>()
            activity?.overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListeners() {
        swipeRefreshLayoutSections.setOnRefreshListener {
            AnalyticsUtils.homeRefreshed(requireContext())
            refreshMethod()
        }
    }

    private fun initAdapter(sections: List<Section>) {
        if (adapter == null) {
            adapter = AdapterSections(prefUtils, sections, {
                // TODO add this later
            }, {
                startActivityWebcamDetail(it)
            })

            recyclerViewSections.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@FragmentMain.adapter

                // Some optimizations
                setHasFixedSize(true)
            }
        } else {
            adapter?.refreshData(sections)
        }
    }

    private fun getData() {
        Single.zip(
            viewModelMain.getSectionsSingle(),
            viewModelMain.getWebcamsSingle(),
            BiFunction { sections: List<Section>, webcams: List<Webcam> ->
                sections to webcams
            })
            .fromIOToMain()
            .subscribe({ sectionsAndWebcams ->
                sectionsAndWebcams.first.forEach { section ->
                    section.webcams = sectionsAndWebcams.second.filter { it.sectionUid == section.uid }
                }

                // Init recyclerView adapter and layoutManager
                initAdapter(sectionsAndWebcams.first)
            }, {
                Timber.e(it, "Error when getting sections and webcams")
            }).addTo(disposables)
    }

    private fun refreshMethod() {
        // Get new data
        viewModelMain.updateData().doFinally {
            swipeRefreshLayoutSections.isRefreshing = false
        }.subscribe({
            getData()
            Timber.d("Sections refreshed correctly")
        }, {
            // TODO Show error to user
            Timber.e(it, "Error when getting sections from PTR")
        }).addTo(disposables)
    }

    private fun startActivityWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")

        requireContext().startActivity(
            ActivityWebcam.getIntent(
                requireContext(),
                webcam
            )
        )
        activity?.overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }
}