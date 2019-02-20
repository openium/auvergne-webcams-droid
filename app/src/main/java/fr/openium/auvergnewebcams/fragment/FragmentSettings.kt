package fr.openium.auvergnewebcams.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivitySettingsAbout
import fr.openium.auvergnewebcams.dialog.RefreshDelayPickerDialog
import fr.openium.auvergnewebcams.event.eventNewValueDelay
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber


/**
 * Created by laura on 04/12/2017.
 */
class FragmentSettings : AbstractFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_settings

    // --- ANALYTICS ---
    // ---------------------------------------------------

    private fun sendAnalyticsRefreshAuto(isChecked: Boolean) {
        AnalyticsUtils.setUserPropertiesRefreshPreferences(requireContext(), isChecked)
    }

    private fun sendAnalyticsRefreshInterval(refreshIntervalValue: Int) {
        AnalyticsUtils.setUserPropertiesRefreshIntervalPreferences(requireContext(), refreshIntervalValue)
    }

    private fun sendAnalyticsWebcamQuality(isChecked: Boolean) {
        if (isChecked) {
            AnalyticsUtils.setUserPropertiesWebcamQualityPreferences(requireContext(), "high")
        } else {
            AnalyticsUtils.setUserPropertiesWebcamQualityPreferences(requireContext(), "low")
        }
    }

    private fun sendAnalyticsAboutClicked() {
        AnalyticsUtils.buttonAboutClicked(requireContext())
    }

    private fun sendAnalyticsOpeniumWebsiteClicked() {
        AnalyticsUtils.buttonWebsiteOpeniumClicked(requireContext())
    }

    private fun sendAnalyticsLesPiratesClicked() {
        AnalyticsUtils.buttonLesPiratesClicked(requireContext())
    }

    private fun sendAnalyticsSuggestWebcamClicked() {
        AnalyticsUtils.buttonSuggestWebcamClicked(requireContext())
    }

    private fun sendAnalyticsRateAppClicked() {
        AnalyticsUtils.buttonRateAppClicked(requireContext())
    }

    // --- LIFE CYCLE ---
    // ---------------------------------------------------

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setListeners()

        //Refresh auto
        switchSettingsRefreshDelay.isChecked = PreferencesAW.isWebcamsDelayRefreshActive(requireContext())
        showDelayRefresh(PreferencesAW.isWebcamsDelayRefreshActive(requireContext()))

        //Delay of auto refresh
        textViewSettingsDelayValue.text = PreferencesAW.getWebcamsDelayRefreshValue(requireContext()).toString()

        //Quality of webcams
        switchSettingsQualityWebcams.isChecked = PreferencesAW.isWebcamsHighQuality(requireContext())

        initVersion()
    }

    private fun setListeners() {
        //Refresh auto
        switchSettingsRefreshDelay.setOnCheckedChangeListener { _, isChecked ->
            //Analytics
            sendAnalyticsRefreshAuto(isChecked)

            PreferencesAW.setWebcamsDelayRefreshActive(requireContext(), isChecked)
            showDelayRefresh(isChecked)
        }

        //Delay of auto refresh
        linearLayoutSettingsDelayRefresh.setOnClickListener {
            val numberPickerDialog = RefreshDelayPickerDialog.newInstance(PreferencesAW.getWebcamsDelayRefreshValue(requireContext()))
            childFragmentManager.beginTransaction()
                .add(numberPickerDialog, "dialog_picker")
                .commitAllowingStateLoss()
        }

        eventNewValueDelay.subscribe {
            //Analytics
            sendAnalyticsRefreshInterval(it)

            PreferencesAW.setWebcamsDelayRefreshValue(requireContext(), it)
            textViewSettingsDelayValue.text = it.toString()
        }.addTo(disposables)

        //Quality of webcams
        switchSettingsQualityWebcams.setOnCheckedChangeListener { _, isChecked ->
            //Analytics
            sendAnalyticsWebcamQuality(isChecked)

            PreferencesAW.setWebcamsHighQuality(requireContext(), isChecked)
        }

        //Other info about AW
        textViewSettingsAbout.setOnClickListener {
            //Analytics
            sendAnalyticsAboutClicked()

            startActivity<ActivitySettingsAbout>()
            activity?.overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
        }

        textViewSettingsOpenium.setOnClickListener {
            //Analytics
            sendAnalyticsOpeniumWebsiteClicked()

            startActivityForUrl(getString(R.string.url_openium))
        }

        textViewSettingsPirates.setOnClickListener {
            //Analytics
            sendAnalyticsLesPiratesClicked()

            startActivityForUrl(getString(R.string.url_pirates))
        }

        textViewSettingsSendNewWebcam.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_send_new_webcam_title)
                .setMessage(R.string.settings_send_new_webcam_message)
                .setNeutralButton(R.string.generic_ok) { dialog, _ ->
                    //Analytics
                    sendAnalyticsSuggestWebcamClicked()

                    sendEmail()
                    dialog.dismiss()
                }.show()
        }

        textViewSettingsNote.setOnClickListener {
            //Analytics
            sendAnalyticsRateAppClicked()

            startActivityForUrl(getString(R.string.url_note, requireContext().packageName))
        }
    }

    // --- OTHER ---
    // ---------------------------------------------------

    private fun sendEmail() {
        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse("mailto:${getString(R.string.detail_signal_problem_email)}")
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_send_new_webcam_email_title))
        intentEmail.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_send_new_webcam_email_message))


        if (intentEmail.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intentEmail, 1)
        } else {
            showMessage(R.string.settings_send_new_webcam_email_no_app_found, Snackbar.LENGTH_SHORT)
        }
    }

    private fun showDelayRefresh(show: Boolean) {
        if (show) {
            linearLayoutSettingsDelayRefresh.show()
        } else {
            linearLayoutSettingsDelayRefresh.gone()
        }
    }

    private fun startActivityForUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
        } else {
            snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
        }
    }

    private fun initVersion() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            if (packageInfo != null) {
                val version = getString(R.string.settings_version, packageInfo.versionName, packageInfo.versionCode.toString())
                textViewSettingsVersion.text = version
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }
    }
}