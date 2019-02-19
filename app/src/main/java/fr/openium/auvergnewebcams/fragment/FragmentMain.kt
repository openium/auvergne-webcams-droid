package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.utils.AnalyticsUtils


class FragmentMain : AbstractFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_main

    // --- ANALYTICS ---
    // ---------------------------------------------------

    private fun sendAllAnalyticsData() {
        //Analytics
        context?.let {
            AnalyticsUtils.appIsOpen(it)
            AnalyticsUtils.sendAllUserPreferences(it)
        }
    }

    // --- LIFE CYCLE ---
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendAllAnalyticsData()

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    // ---------------------------------------------------
    // --- OTHER JOB ---
    // ---------------------------------------------------

}