package fr.openium.auvergnewebcams.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.utils.FirebaseUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val prefUtils: PreferencesUtils by inject()

    private val _isWebcamsHighQuality = MutableStateFlow(prefUtils.isWebcamsHighQuality)
    val isWebcamsHighQuality: StateFlow<Boolean> = _isWebcamsHighQuality

    fun onQualityChanged(isChecked: Boolean, context: Context) {
        FirebaseUtils.setUserPropertiesWebcamQualityPreferences(
            context,
            if (isChecked) "high" else "low"
        )
        prefUtils.isWebcamsHighQuality = isChecked
        _isWebcamsHighQuality.value = isChecked
    }

    fun sendEmail(context: Context) {
        val intentEmail = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${context.getString(R.string.detail_signal_problem_email)}")
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.settings_send_new_webcam_email_title)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.settings_send_new_webcam_email_message)
            )
        }
        val chooser = Intent.createChooser(intentEmail, context.getString(R.string.generic_chooser))
        if (chooser.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.generic_no_email_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

