package fr.openium.auvergnewebcams.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettingsAbout
import fr.openium.auvergnewebcams.dialog.NumberPickerDialog
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.applicationContext
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
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

        //Refresh auto
        switch_refresh_delay.setOnCheckedChangeListener { _, isChecked ->
            PreferencesAW.setWebcamsDelayRefreshActive(applicationContext!!, isChecked)
            showDelayRefresh(isChecked)

            //Analytics
            AnalyticsUtils.setUserPropertiesRefreshPreferences(context!!, isChecked)
        }

        switch_refresh_delay.isChecked = PreferencesAW.isWebcamsDelayRefreshActive(applicationContext!!)
        showDelayRefresh(PreferencesAW.isWebcamsDelayRefreshActive(applicationContext!!))

        oneTimeDisposables.add(Events.eventNewValueDelay
                .obs
                .fromIOToMain()
                .subscribe {
                    if (isAlive) {
                        PreferencesAW.setWebcamsDelayRefreshValue(applicationContext!!, it)
                        textview_delay_value.text = it.toString()

                        //Analytics
                        AnalyticsUtils.setUserPropertiesRefreshIntervalPreferences(context!!, it)
                    }
                })

        //Delay of auto refresh
        textview_delay_value.text = PreferencesAW.getWebcamsDelayRefreshValue(applicationContext!!).toString()

        linearlayout_delay_refresh.setOnClickListener {
            val numberPickerDialog = NumberPickerDialog.newInstance(PreferencesAW.getWebcamsDelayRefreshValue(applicationContext!!))
            childFragmentManager.beginTransaction()
                    .add(numberPickerDialog, "dialog_picker")
                    .commitAllowingStateLoss()
        }

        //Quality of webcams
        switch_quality_webcams.isChecked = PreferencesAW.isWebcamsHighQuality(applicationContext!!)
        switch_quality_webcams.setOnCheckedChangeListener { _, isChecked ->
            PreferencesAW.setWebcamsHighQuality(applicationContext!!, isChecked)
            if (isChecked) {
                AnalyticsUtils.setUserPropertiesWebcamQualityPreferences(context!!, "high")
            } else {
                AnalyticsUtils.setUserPropertiesWebcamQualityPreferences(context!!, "low")
            }
        }

        //Other info about Auvergne Webcam
        textview_about.setOnClickListener {
            //Analytics
            AnalyticsUtils.buttonAboutClicked(context!!)

            val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext!!, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
            startActivity(Intent(applicationContext, ActivitySettingsAbout::class.java), bundle)
        }
        textview_openium.setOnClickListener {
            //Analytics
            AnalyticsUtils.buttonWebsiteOpeniumClicked(context!!)

            startActivityForUrl(getString(R.string.url_openium))
        }
        textview_pirates.setOnClickListener {
            //Analytics
            AnalyticsUtils.buttonLesPiratesClicked(context!!)

            startActivityForUrl(getString(R.string.url_pirates))
        }
        textview_send_new_webcam.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(R.string.settings__send_new_webcam_title)
                    .setMessage(R.string.settings__send_new_webcam_message)
                    .setNeutralButton(R.string.generic_ok, { dialog, _ ->
                        AnalyticsUtils.buttonProposeWebcamClicked(context!!)
                        sendEmail()
                        dialog.dismiss()
                    })
                    .show()
        }
        textview_note.setOnClickListener {
            //Analytics
            AnalyticsUtils.buttonRateAppClicked(context!!)

            startActivityForUrl(getString(R.string.url_note, applicationContext!!.packageName))
        }

        //Init version
        initVersion()
    }

    private fun sendEmail() {
        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse("mailto:${getString(R.string.detail_signal_problem_email)}") // only email apps should handle this
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings__send_new_webcam_email_title))
        intentEmail.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings__send_new_webcam_email_message))

        if (intentEmail.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(intentEmail, 1)
        } else {
            Snackbar.make(view!!.findViewById(android.R.id.content), R.string.settings__send_new_webcam_email_no_app_found, Snackbar.LENGTH_SHORT).show()
        }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun showDelayRefresh(show: Boolean) {
        if (show) {
            linearlayout_delay_refresh.show()
        } else {
            linearlayout_delay_refresh.gone()
        }
    }

    private fun startActivityForUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext!!, R.anim.animation_from_right, R.anim.animation_to_left).toBundle()
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent, bundle)
        } else {
            snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

    private fun initVersion() {
        try {
            val packageInfo = applicationContext!!.getPackageManager().getPackageInfo(applicationContext!!.getPackageName(), 0)
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