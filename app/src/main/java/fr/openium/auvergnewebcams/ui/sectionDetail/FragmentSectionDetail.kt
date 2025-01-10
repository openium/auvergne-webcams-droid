package fr.openium.auvergnewebcams.ui.sectionDetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.mapSection.ActivityMapSection
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.kotlintools.ext.setTitle
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_section_detail.recyclerViewSectionDetail
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSectionDetail : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_section_detail

    private lateinit var viewModelSectionDetail: ViewModelSectionDetail

    private lateinit var section: Section
    private var adapterSectionDetail: AdapterSectionDetail? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelSectionDetail = ViewModelProvider(this)[ViewModelSectionDetail::class.java]
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sectionId = arguments?.getLong(KEY_SECTION_ID, -1L) ?: -1L

        if (sectionId != -1L) {
            setListener(sectionId)
        } else requireActivity().finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_section_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_map -> {
                startActivity(
                    ActivityMapSection.getIntent(
                        requireContext(),
                        sectionId = section.uid
                    )
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    // --- Methods
    // ---------------------------------------------------

    private fun setListener(sectionId: Long) {
        Single.zip(
            viewModelSectionDetail.getSectionSingle(sectionId),
            viewModelSectionDetail.getWebcamsSingle(sectionId)
        ) { section: Optional<Section>, webcams: List<Webcam> ->
            section to webcams
        }.fromIOToMain()
            .subscribe({ sectionAndWebcams ->
                sectionAndWebcams.first.value?.let {
                    section = it
                    section.webcams = sectionAndWebcams.second
                    setTitle(section.title ?: "")
                    initAdapter()
                }
            }, {
                Timber.e(it, "Error when getting sections and webcams")
            }).addTo(disposables)
    }

    private fun initAdapter() {
        if (adapterSectionDetail == null) {
            adapterSectionDetail = AdapterSectionDetail(getData()) {
                AnalyticsUtils.webcamDetailsClicked(requireContext(), it.title ?: "")
                requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), it))
            }

            recyclerViewSectionDetail.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapterSectionDetail

                // Optimize
                setHasFixedSize(true)
            }
        } else {
            adapterSectionDetail?.refreshData(getData())
        }
    }

    private fun getData(): List<AdapterSectionDetail.Data> =
        mutableListOf<AdapterSectionDetail.Data>().apply {
            // First add section
            add(AdapterSectionDetail.Data(section))

            // Then add all webcams
            section.webcams.forEach {
                add(AdapterSectionDetail.Data(webcam = it))
            }
        }
}