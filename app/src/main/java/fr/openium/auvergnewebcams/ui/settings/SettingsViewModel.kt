package fr.openium.auvergnewebcams.ui.settings

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.about.ActivitySettingsAbout
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.FirebaseUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KClass

class SettingsViewModel : ViewModel(), KoinComponent {

    private val context: Context by inject()

    private val prefUtils: PreferencesUtils by inject()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var isWebcamsHighQuality by mutableStateOf(prefUtils.isWebcamsHighQuality)
        private set

    fun onQualityChanged(isChecked: Boolean) {
        FirebaseUtils.setUserPropertiesWebcamQualityPreferences(
            context,
            if (isChecked) "high" else "low"
        )
        prefUtils.isWebcamsHighQuality = isChecked
        isWebcamsHighQuality = isChecked
    }

    var isDelayRefreshActive by mutableStateOf(prefUtils.isWebcamsDelayRefreshActive)
        private set

    fun onDelayRefreshChanged(isChecked: Boolean) {
        FirebaseUtils.setUserPropertiesRefreshPreferences(context, isChecked)
        prefUtils.isWebcamsDelayRefreshActive = isChecked
        isDelayRefreshActive = isChecked
    }

    var refreshDelay by mutableStateOf(prefUtils.webcamsDelayRefreshValue)
        private set

    fun onRefreshDelayChanged(newDelay: Int) {
        FirebaseUtils.setUserPropertiesRefreshIntervalPreferences(context, newDelay)
        prefUtils.webcamsDelayRefreshValue = newDelay
        refreshDelay = newDelay
    }


    fun getAppVersion(): String {
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

    fun onAboutClicked() {
        AnalyticsUtils.aboutClicked(context)
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.ToActivity(ActivitySettingsAbout::class))
        }
    }

    fun onOpeniumClicked() {
        AnalyticsUtils.websiteOpeniumClicked(context)
        viewModelScope.launch {
            val url = context.getString(R.string.url_openium)
            _navigationEvent.emit(NavigationEvent.ToUrl(url))
        }
    }

    fun onLesPiratesClicked() {
        AnalyticsUtils.lesPiratesClicked(context)
        viewModelScope.launch {
            val url = context.getString(R.string.url_pirates)
            _navigationEvent.emit(NavigationEvent.ToUrl(url))
        }
    }

    fun onRateClicked() {
        AnalyticsUtils.rateAppClicked(context)
        viewModelScope.launch {
            val url = context.getString(
                R.string.url_note_format,
                context.packageName
            )
            _navigationEvent.emit(NavigationEvent.ToUrl(url))
        }
    }
}

sealed class NavigationEvent {
    data class ToActivity(val activityClass: KClass<out Activity>) : NavigationEvent()
    data class ToUrl(val url: String) : NavigationEvent()
}