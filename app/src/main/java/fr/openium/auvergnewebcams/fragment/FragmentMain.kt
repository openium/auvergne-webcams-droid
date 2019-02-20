package fr.openium.auvergnewebcams.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettings
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.startActivity


class FragmentMain : AbstractFragment() {

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

}