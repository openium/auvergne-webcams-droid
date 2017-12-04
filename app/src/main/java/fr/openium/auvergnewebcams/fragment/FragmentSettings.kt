package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettingsAbout
import fr.openium.auvergnewebcams.ext.applicationContext
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber

/**
 * Created by laura on 04/12/2017.
 */
class FragmentSettings : AbstractFragment() {

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVersion()
        textview_about.setOnClickListener {
            startActivity(Intent(applicationContext, ActivitySettingsAbout::class.java))
        }
        textview_openium.setOnClickListener { }
        textview_pirates.setOnClickListener { }
        textview_note.setOnClickListener { }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initVersion() {
        try {
            val packageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0)
            if (packageInfo != null) {
                val version = getString(R.string.settings_version, packageInfo.versionName, packageInfo.versionCode.toString())
                textview_version.setText(version)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e.message)
        }
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_settings

}