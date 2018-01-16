package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.preference.PreferenceManager
import fr.openium.auvergnewebcams.ext.toUnixTimestamp

/**
 * Created by laura on 04/01/2018.
 */
object PreferencesAW {

    const val DEFAUT_TIME_DELAY = 10

    private const val KEY_WEBCAM_QUALITY = "KEY_WEBCAM_QUALITY"
    private const val KEY_WEBCAM_DELAY_REFRESH = "KEY_WEBCAM_DELAY_REFRESH"
    private const val KEY_WEBCAM_DELAY_REFRESH_VALUE = "KEY_WEBCAM_DELAY_REFRESH_VALUE"
    private const val KEY_WEBCAM_LAST_UPDATE_TIMESTAMP = "KEY_WEBCAM_LAST_UPDATE_TIMESTAMP"


    fun setWebcamsHighQuality(context: Context, isHighQuality: Boolean) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putBoolean(KEY_WEBCAM_QUALITY, isHighQuality).apply()
    }

    fun isWebcamsHighQuality(context: Context): Boolean {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getBoolean(KEY_WEBCAM_QUALITY, true)
    }

    fun setWebcamsDelayRefreshActive(context: Context, isDelayRefreshActive: Boolean) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putBoolean(KEY_WEBCAM_DELAY_REFRESH, isDelayRefreshActive).apply()
    }

    fun isWebcamsDelayRefreshActive(context: Context): Boolean {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getBoolean(KEY_WEBCAM_DELAY_REFRESH, true)
    }

    fun setWebcamsDelayRefreshValue(context: Context, value: Int) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, value).apply()
    }

    fun getWebcamsDelayRefreshValue(context: Context): Int {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getInt(KEY_WEBCAM_DELAY_REFRESH_VALUE, DEFAUT_TIME_DELAY)
    }

    fun getLastUpdateWebcamsTimestamp(context: Context): Long {
        var lastUpdate = 0L
        if (isWebcamsDelayRefreshActive(context)) {
            lastUpdate = getLastUpdateTimestamp(context)
            if (lastUpdate == -1L) {
                lastUpdate = System.currentTimeMillis().toUnixTimestamp()
                setLastUpdateTimestamp(context, lastUpdate)
            } else {
                val delayRefreshInSec = getWebcamsDelayRefreshValue(context) * 60
                val diff = System.currentTimeMillis().toUnixTimestamp() - lastUpdate
                if (diff > delayRefreshInSec) {
                    lastUpdate = System.currentTimeMillis().toUnixTimestamp()
                    setLastUpdateTimestamp(context, lastUpdate)
                }
            }
        }
        return lastUpdate
    }

    fun setLastUpdateTimestamp(context: Context, lastUpdateTimestamp: Long) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putLong(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, lastUpdateTimestamp).apply()
    }

    private fun getLastUpdateTimestamp(context: Context): Long {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getLong(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, -1L)
    }
}