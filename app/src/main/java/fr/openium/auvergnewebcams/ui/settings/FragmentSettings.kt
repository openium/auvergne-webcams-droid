package fr.openium.auvergnewebcams.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.event.eventNewRefreshDelayValue
import fr.openium.auvergnewebcams.event.eventRefreshDelayValueChanged
import fr.openium.auvergnewebcams.ui.about.ActivitySettingsAbout
import fr.openium.auvergnewebcams.ui.settings.components.SettingsScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.FirebaseUtils
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.compose_view.*
import timber.log.Timber


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSettings : AbstractFragment() {

    override val layoutId: Int = R.layout.compose_view

    // --- Life cycle
    // ---------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        composeView.setContent {
            var isWebcamsDelayRefreshActive by remember {
                mutableStateOf(prefUtils.isWebcamsDelayRefreshActive)
            }
            var webcamsDelayRefreshValue by remember {
                mutableStateOf(prefUtils.webcamsDelayRefreshValue)
            }
            var isWebcamsHighQuality by remember {
                mutableStateOf(prefUtils.isWebcamsHighQuality)
            }

            eventNewRefreshDelayValue.subscribe {
                FirebaseUtils.setUserPropertiesRefreshIntervalPreferences(requireContext(), it)
                prefUtils.webcamsDelayRefreshValue = it
                eventRefreshDelayValueChanged.accept(Unit)
                webcamsDelayRefreshValue = it
            }.addTo(disposables)

            AWTheme {
                SettingsScreen(
                    isWebcamsDelayRefreshActive = isWebcamsDelayRefreshActive,
                    changeSettingsRefreshDelay = { isChecked ->
                        FirebaseUtils.setUserPropertiesRefreshPreferences(requireContext(), isChecked)
                        prefUtils.isWebcamsDelayRefreshActive = isChecked
                        isWebcamsDelayRefreshActive = isChecked
                    },
                    webcamsDelayRefreshValue = webcamsDelayRefreshValue,
                    changeWebcamDelayRefreshValue = {
                        val numberPickerDialog = RefreshDelayPickerDialog.newInstance(prefUtils.webcamsDelayRefreshValue)
                        childFragmentManager.beginTransaction()
                            .add(numberPickerDialog, "dialog_picker")
                            .commitAllowingStateLoss()
                    },
                    isWebcamsHighQuality = isWebcamsHighQuality,
                    changeWebcamHighQuality = { isChecked ->
                        FirebaseUtils.setUserPropertiesWebcamQualityPreferences(requireContext(), if (isChecked) "high" else "low")
                        prefUtils.isWebcamsHighQuality = isChecked
                        isWebcamsHighQuality = isChecked
                    },
                    navigateToAbout = {
                        AnalyticsUtils.aboutClicked(requireContext())
                        startActivity<ActivitySettingsAbout>()
                    },
                    navigateToOpeniumWebsite = {
                        AnalyticsUtils.websiteOpeniumClicked(requireContext())
                        startActivityForUrl(getString(R.string.url_openium))
                    },
                    navigateToPiratesWebsite = {
                        AnalyticsUtils.lesPiratesClicked(requireContext())
                        startActivityForUrl(getString(R.string.url_pirates))
                    },
                    proposeNewWebcam = {
                        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme)
                            .setTitle(R.string.settings_send_new_webcam_title)
                            .setMessage(R.string.settings_send_new_webcam_message)
                            .setNeutralButton(R.string.generic_ok) { dialog, _ ->
                                AnalyticsUtils.suggestWebcamClicked(requireContext())
                                sendEmail()
                                dialog.dismiss()
                            }.show()
                    },
                    rateApp = {
                        AnalyticsUtils.rateAppClicked(requireContext())
                        startActivityForUrl(getString(R.string.url_note_format, requireContext().packageName))
                    },
                    version = getVersion()
                )
            }
        }

    }

    // --- Methods
    // ---------------------------------------------------


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

    private fun getVersion(): String {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            getString(
                R.string.settings_version_format,
                packageInfo.versionName,
                PackageInfoCompat.getLongVersionCode(packageInfo).toString()
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            ""
        }
    }
}