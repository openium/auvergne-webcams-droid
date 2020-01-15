package fr.openium.auvergnewebcams.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.settings.ActivitySettings
import fr.openium.auvergnewebcams.ui.webcamdetail.ActivityWebcam
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.startActivity
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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

        AnalyticsUtils.appIsOpen(requireContext())
        AnalyticsUtils.sendAllUserProperties(requireContext())

        viewModelMain = ViewModelProviders.of(this).get(ViewModelMain::class.java)

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set listeners
        setListeners()

        // Init recyclerView adapter and layoutManager
        initAdapter()
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

    override fun startSubscription(onStartDisposables: CompositeDisposable) {
        super.startSubscription(onStartDisposables)

        Observable.combineLatest(
            viewModelMain.getSectionsObs(),
            viewModelMain.getWebcamsObs(),
            BiFunction { sections: List<Section>, webcams: List<Webcam> ->
                sections to webcams
            })
            .fromIOToMain().subscribe({ sectionsToWebcams ->
                sectionsToWebcams.first.forEach { section ->
                    section.webcams = sectionsToWebcams.second.filter { it.sectionUid == section.uid }
                }

                Timber.d("list count ${sectionsToWebcams.first.count()}")

                // Update data display
                adapter?.refreshData(sectionsToWebcams.first)
            }, {
                Timber.e(it, "Error on getting Sections/webcams from realm")
            }).addTo(disposables)
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListeners() {
        swipeRefreshLayoutSections.setOnRefreshListener {
            AnalyticsUtils.homeRefreshed(requireContext())
            refreshMethod()
        }
    }

    private fun initAdapter() {
        adapter = AdapterSections(requireContext(), arrayListOf(), {
            // TODO add this later
        }, {
            startActivityWebcamDetail(it)
        })

        recyclerViewSections.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FragmentMain.adapter

            // Some optimizations
            setHasFixedSize(true)
            setItemViewCacheSize(3)

            (layoutManager as? LinearLayoutManager)?.also {
                it.initialPrefetchItemCount = 3
            }
        }
    }

    private fun refreshMethod() {
        // Get new data
        viewModelMain.updateData().doFinally {
            swipeRefreshLayoutSections.isRefreshing = false
        }.subscribe({
            Timber.d("Sections refreshed correctly")
        }, {
            Timber.e(it, "Error when getting sections from PTR")
        }).addTo(disposables)
    }

    private fun startActivityWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")

        requireContext().startActivity(Intent(context, ActivityWebcam::class.java).apply {
            putExtra(Constants.KEY_ID, webcam.uid)
            putExtra(Constants.KEY_TYPE, webcam.type)
        }, ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.animation_from_right, R.anim.animation_to_left).toBundle())
    }
}