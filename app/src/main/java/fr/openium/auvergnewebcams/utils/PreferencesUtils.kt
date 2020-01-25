package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import fr.openium.kotlintools.ext.toUnixTimestamp

/**
 * Created by Openium on 19/02/2019.
 */
class PreferencesUtils(val context: Context) {

    var webcamsDelayRefreshValue: Int
        get() = getInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, DEFAULT_TIME_DELAY)
        set(accepted) {
            edit {
                putInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, accepted)
            }
        }

    var isWebcamsDelayRefreshActive: Boolean
        get() = getBoolean(KEY_WEBCAM_DELAY_REFRESH, true)
        set(accepted) {
            edit {
                putBoolean(KEY_WEBCAM_DELAY_REFRESH, accepted)
            }
        }

    var isWebcamsHighQuality: Boolean
        get() = getBoolean(KEY_WEBCAM_QUALITY, true)
        set(accepted) {
            edit {
                putBoolean(KEY_WEBCAM_QUALITY, accepted)
            }
        }

    var lastUpdateWebcamsTimestamp: Long
        get() {
            var lastUpdate = getLong(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, -1)
            if (isWebcamsDelayRefreshActive) {
                if (lastUpdate == -1L) {
                    lastUpdate = System.currentTimeMillis().toUnixTimestamp()
                    lastUpdateWebcamsTimestamp = lastUpdate
                } else {
                    val delayRefreshInSec = webcamsDelayRefreshValue * 60
                    val diff = System.currentTimeMillis().toUnixTimestamp() - lastUpdate
                    if (diff > delayRefreshInSec) {
                        lastUpdate = System.currentTimeMillis().toUnixTimestamp()
                        lastUpdateWebcamsTimestamp = lastUpdate
                    }
                }
            }
            return lastUpdate
        }
        set(value) {
            edit {
                putLong(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, value)
            }
        }

    var newNotifId: Int
        get() {
            val value = getInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, DEFAULT_TIME_DELAY)
            newNotifId = value + 1
            return value
        }
        set(accepted) {
            edit {
                putInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, accepted)
            }
        }

    private fun getString(key: String, defaultValue: String? = null): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun getStringSet(key: String, defaultValue: MutableSet<String> = mutableSetOf()): MutableSet<String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    private fun getLong(key: String, defaultValue: Long = 0L): Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getLong(key, defaultValue)
    }

    private fun getInt(key: String, defaultValue: Int = 0): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(key, defaultValue)
    }

    private fun edit(block: SharedPreferences.Editor.() -> Unit) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit {
            block.invoke(this)
        }
    }

    companion object {
        const val DEFAULT_TIME_DELAY = 10

        // Common
        private const val KEY_WEBCAM_QUALITY = "KEY_WEBCAM_QUALITY"
        private const val KEY_WEBCAM_DELAY_REFRESH = "KEY_WEBCAM_DELAY_REFRESH"
        private const val KEY_WEBCAM_DELAY_REFRESH_VALUE = "KEY_WEBCAM_DELAY_REFRESH_VALUE"
        private const val KEY_WEBCAM_LAST_UPDATE_TIMESTAMP = "KEY_WEBCAM_LAST_UPDATE_TIMESTAMP"
    }
}