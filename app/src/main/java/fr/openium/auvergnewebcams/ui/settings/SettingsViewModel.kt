package fr.openium.auvergnewebcams.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
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

    private val _isDelayRefreshActive = MutableStateFlow(prefUtils.isWebcamsDelayRefreshActive)
    val isDelayRefreshActive: StateFlow<Boolean> = _isDelayRefreshActive


    fun onDelayRefreshChanged(isChecked: Boolean, context: Context) {
        FirebaseUtils.setUserPropertiesRefreshPreferences(context, isChecked)
        prefUtils.isWebcamsDelayRefreshActive = isChecked
        _isDelayRefreshActive.value = isChecked
    }

    private val _refreshDelay = MutableStateFlow(prefUtils.webcamsDelayRefreshValue)
    val refreshDelay: StateFlow<Int> = _refreshDelay

    fun onRefreshDelayChanged(newDelay: Int, context: Context) {
        FirebaseUtils.setUserPropertiesRefreshIntervalPreferences(context, newDelay)
        prefUtils.webcamsDelayRefreshValue = newDelay
        _refreshDelay.value = newDelay
    }
    

}

