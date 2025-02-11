package fr.openium.auvergnewebcams.ui.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.utils.FirebaseUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {
    
    private val prefUtils: PreferencesUtils by inject()

    var isWebcamsHighQuality by mutableStateOf(prefUtils.isWebcamsHighQuality)
        private set

    fun onQualityChanged(isChecked: Boolean, context: Context) {
        FirebaseUtils.setUserPropertiesWebcamQualityPreferences(
            context,
            if (isChecked) "high" else "low"
        )
        prefUtils.isWebcamsHighQuality = isChecked
        isWebcamsHighQuality = isChecked
    }

    var isDelayRefreshActive by mutableStateOf(prefUtils.isWebcamsDelayRefreshActive)
        private set

    fun onDelayRefreshChanged(isChecked: Boolean, context: Context) {
        FirebaseUtils.setUserPropertiesRefreshPreferences(context, isChecked)
        prefUtils.isWebcamsDelayRefreshActive = isChecked
        isDelayRefreshActive = isChecked
    }

    var refreshDelay by mutableStateOf(prefUtils.webcamsDelayRefreshValue)
        private set

    fun onRefreshDelayChanged(newDelay: Int, context: Context) {
        FirebaseUtils.setUserPropertiesRefreshIntervalPreferences(context, newDelay)
        prefUtils.webcamsDelayRefreshValue = newDelay
        refreshDelay = newDelay
    }


    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            context.getString(
                R.string.settings_version_format,
                packageInfo.versionName,
                PackageInfoCompat.getLongVersionCode(packageInfo).toString()
            )
        } catch (e: Exception) {
            ""
        }
    }


}

