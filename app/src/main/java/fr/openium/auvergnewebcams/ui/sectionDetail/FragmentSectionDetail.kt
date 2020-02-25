package fr.openium.auvergnewebcams.ui.sectionDetail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_section_detail.*
import timber.log.Timber


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSectionDetail : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_section_detail

    protected lateinit var viewModelSectionDetail: ViewModelSectionDetail

    protected lateinit var section: Section
    private var adapterSectionDetail: AdapterSectionDetail? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelSectionDetail = ViewModelProvider(this).get(ViewModelSectionDetail::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sectionId = arguments?.getLong(Constants.KEY_SECTION_ID, -1L) ?: -1L

        if (sectionId != -1L) {
            setListener(sectionId)
        } else requireActivity().finish()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListener(sectionId: Long) {
        Single.zip(
            viewModelSectionDetail.getSectionSingle(sectionId),
            viewModelSectionDetail.getWebcamsSingle(sectionId),
            BiFunction { section: Optional<Section>, webcams: List<Webcam> ->
                section to webcams
            })
            .fromIOToMain()
            .subscribe({ sectionAndWebcams ->
                sectionAndWebcams.first.value?.let {
                    section = it
                    section.webcams = sectionAndWebcams.second

                    initAdapter()
                }
            }, {
                Timber.e(it, "Error when getting sections and webcams")
            }).addTo(disposables)
    }

    private fun initAdapter() {
        if (adapterSectionDetail == null) {
            adapterSectionDetail = AdapterSectionDetail(prefUtils, dateUtils, Glide.with(requireContext()), getData()) {
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