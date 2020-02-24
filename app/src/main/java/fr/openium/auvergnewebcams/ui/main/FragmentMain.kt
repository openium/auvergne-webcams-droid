package fr.openium.auvergnewebcams.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.search.ActivitySearch
import fr.openium.auvergnewebcams.ui.sectionDetail.ActivitySectionDetail
import fr.openium.auvergnewebcams.ui.settings.ActivitySettings
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.ImageUtils
import fr.openium.kotlintools.ext.applicationContext
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


class FragmentMain : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_main

    private lateinit var viewModelMain: ViewModelMain

    private var adapterMain: AdapterMainSections? = null

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

        textViewSearch.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                textViewSearch,
                getString(R.string.transition_search_name)
            )
            startActivity(Intent(applicationContext, ActivitySearch::class.java), options.toBundle())
        }
    }

    private fun initAdapter(sections: List<Section>) {
        if (adapterMain == null) {
            adapterMain = AdapterMainSections(prefUtils, dateUtils, getAdapterData(sections), { idToName ->
                AnalyticsUtils.webcamDetailsClicked(requireContext(), idToName.second)
                requireContext().startActivity(ActivitySectionDetail.getIntent(requireContext(), idToName.first))
            }, {
                AnalyticsUtils.webcamDetailsClicked(requireContext(), it.title ?: "")
                requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), it))
            })

            recyclerViewSections.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    setItemViewCacheSize(0)
                    recycleChildrenOnDetach = true
                }
                adapter = adapterMain

                // Optimize
                setHasFixedSize(true)
            }
        } else {
            adapterMain?.refreshData(getAdapterData(sections))
        }
    }

    private fun getAdapterData(sections: List<Section>): List<AdapterMainSections.Data> {
        val dataList = mutableListOf<AdapterMainSections.Data>()

        sections.forEach {
            // Add header
            dataList.add(
                AdapterMainSections.Data(
                    AdapterMainSections.DataHeader(
                        it.uid,
                        it.title ?: "",
                        ImageUtils.getImageResourceAssociatedToSection(requireContext(), it),
                        String.format(
                            Locale.getDefault(),
                            resources.getQuantityString(
                                R.plurals.nb_cameras_format,
                                it.webcams.count(),
                                it.webcams.count()
                            )
                        )
                    )
                )
            )

            // Add items
            dataList.add(
                AdapterMainSections.Data(
                    webcams = it.webcams
                )
            )
        }

        return dataList
    }

    private fun getData() {
        Single.zip(
            viewModelMain.getSectionsSingle(),
            viewModelMain.getWebcamsSingle(),
            BiFunction { sections: List<Section>, webcams: List<Webcam> ->
                sections.sortedBy { it.order } to webcams.sortedBy { it.order }
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
            if (it is UnknownHostException || it is SocketTimeoutException) {
                snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
            } else {
                snackbar(R.string.generic_error, Snackbar.LENGTH_SHORT)
            }
            Timber.e(it, "Error when getting sections from PTR")
        }).addTo(disposables)
    }
}