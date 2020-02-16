package fr.openium.auvergnewebcams.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.event.eventNewRefreshDelayValue
import fr.openium.auvergnewebcams.event.eventRefreshDelayValueChanged
import fr.openium.auvergnewebcams.ui.about.ActivitySettingsAbout
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.FirebaseUtils
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSettings : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_settings

    // --- Life cycle
    // ---------------------------------------------------

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setListeners()

        initVersion()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListeners() {
        // Auto refresh
        showDelayRefresh(prefUtils.isWebcamsDelayRefreshActive)
        switchSettingsRefreshDelay.isChecked = prefUtils.isWebcamsDelayRefreshActive
        switchSettingsRefreshDelay.setOnCheckedChangeListener { _, isChecked ->
            FirebaseUtils.setUserPropertiesRefreshPreferences(requireContext(), isChecked)
            prefUtils.isWebcamsDelayRefreshActive = isChecked
            showDelayRefresh(isChecked)
        }

        // Auto refresh delay
        linearLayoutSettingsDelayRefresh.setOnClickListener {
            val numberPickerDialog = RefreshDelayPickerDialog.newInstance(prefUtils.webcamsDelayRefreshValue)
            childFragmentManager.beginTransaction()
                .add(numberPickerDialog, "dialog_picker")
                .commitAllowingStateLoss()
        }
        textViewSettingsDelayValue.text = prefUtils.webcamsDelayRefreshValue.toString()

        eventNewRefreshDelayValue.subscribe {
            FirebaseUtils.setUserPropertiesRefreshIntervalPreferences(requireContext(), it)
            prefUtils.webcamsDelayRefreshValue = it
            textViewSettingsDelayValue.text = it.toString()
            eventRefreshDelayValueChanged.accept(Unit)
        }.addTo(disposables)

        // Webcams quality
        switchSettingsQualityWebcams.setOnCheckedChangeListener { _, isChecked ->
            FirebaseUtils.setUserPropertiesWebcamQualityPreferences(requireContext(), if (isChecked) "high" else "low")
            prefUtils.isWebcamsHighQuality = isChecked
        }
        switchSettingsQualityWebcams.isChecked = prefUtils.isWebcamsHighQuality

        // About screen
        textViewSettingsAbout.setOnClickListener {
            AnalyticsUtils.aboutClicked(requireContext())
            startActivity<ActivitySettingsAbout>()
        }

        // Openium website
        textViewSettingsOpenium.setOnClickListener {
            AnalyticsUtils.websiteOpeniumClicked(requireContext())
            startActivityForUrl(getString(R.string.url_openium))
        }

        // Les pirates website
        textViewSettingsPirates.setOnClickListener {
            AnalyticsUtils.lesPiratesClicked(requireContext())
            startActivityForUrl(getString(R.string.url_pirates))
        }

        // Send new webcam
        textViewSettingsSendNewWebcam.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.settings_send_new_webcam_title)
                .setMessage(R.string.settings_send_new_webcam_message)
                .setNeutralButton(R.string.generic_ok) { dialog, _ ->
                    AnalyticsUtils.suggestWebcamClicked(requireContext())
                    sendEmail()
                    dialog.dismiss()
                }.show()
        }

        // Rate app
        textViewSettingsNote.setOnClickListener {
            AnalyticsUtils.rateAppClicked(requireContext())
            startActivityForUrl(getString(R.string.url_note_format, requireContext().packageName))
        }
    }

    private fun sendEmail() {
        val intentEmail = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${getString(R.string.detail_signal_problem_email)}")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_send_new_webcam_email_title))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_send_new_webcam_email_message))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intentEmail, getString(R.string.generic_chooser))

        chooser.resolveActivity(requireContext().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_email_app, Snackbar.LENGTH_SHORT)
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
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

        chooser.resolveActivity(requireContext().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
    }

    private fun initVersion() {
        try {
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)?.also {
                val version =
                    getString(R.string.settings_version_format, it.versionName, PackageInfoCompat.getLongVersionCode(it).toString())
                textViewSettingsVersion.text = version
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }
    }
}