package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettingsAbout
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.snackbar
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
            val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
            startActivity(Intent(applicationContext, ActivitySettingsAbout::class.java), bundle)
        }
        textview_openium.setOnClickListener { startActivityForUrl(getString(R.string.url_openium)) }
        textview_pirates.setOnClickListener { startActivityForUrl(getString(R.string.url_pirates)) }
        textview_note.setOnClickListener { startActivityForUrl(getString(R.string.url_note, applicationContext.packageName)) }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun startActivityForUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent, bundle)
        } else {
            snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

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