package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettings
import fr.openium.auvergnewebcams.adapter.AdapterSections
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.viewmodel.ViewModelMain
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber


class FragmentMain : AbstractFragment() {

    private lateinit var viewModelMain: ViewModelMain

    private var sectionList: ArrayList<Section> = arrayListOf()

    override val layoutId: Int
        get() = R.layout.fragment_main

    // --- ANALYTICS ---
    // ---------------------------------------------------

    private fun sendAnalyticsAppOpened() {
        AnalyticsUtils.appIsOpen(requireContext())
        AnalyticsUtils.sendAllUserPreferences(requireContext())
    }

    private fun sendAnalyticsSettingsClicked() {
        AnalyticsUtils.buttonSettingsClicked(requireContext())
    }

    private fun sendAnalyticsHomeRefreshed() {
        AnalyticsUtils.buttonHomeRefreshed(requireContext())
    }

    // --- LIFE CYCLE ---
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Analytics
        sendAnalyticsAppOpened()

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Get viewModel
        viewModelMain = ViewModelProviders.of(this).get(ViewModelMain::class.java)

        //Get sections
        sectionList.addAll(viewModelMain.getSections())

        //Set listeners
        setListeners()

        initAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_settings) {
            //Analytics
            sendAnalyticsSettingsClicked()

            startActivity<ActivitySettings>()
            activity?.overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // --- OTHER ---
    // ---------------------------------------------------

    private fun setListeners() {
        swipeRefreshLayoutSections.setOnRefreshListener {
            //Analytics
            sendAnalyticsHomeRefreshed()

            refreshMethod()
        }
    }

    private fun refreshMethod() {
        //Get new data
        viewModelMain.updateData().subscribe({
            swipeRefreshLayoutSections.isRefreshing = false
        }, {
            Timber.e(it)
            swipeRefreshLayoutSections.isRefreshing = false
        }).addTo(disposables)
    }

    private fun initAdapter() {
        recyclerViewSections.layoutManager = LinearLayoutManager(context)
        recyclerViewSections.adapter = AdapterSections(sectionList) {
            //TODO add this later
        }
    }
}