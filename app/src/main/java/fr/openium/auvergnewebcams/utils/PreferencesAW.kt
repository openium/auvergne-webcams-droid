package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by laura on 04/01/2018.
 */
object PreferencesAW {

    private const val KEY_WEBCAM_QUALITY = "KEY_WEBCAM_QUALITY"


    fun setWebcamHighQuality(context: Context, isHighQuality: Boolean) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putBoolean(KEY_WEBCAM_QUALITY, isHighQuality).apply()
    }

    fun isWebcamsHighQuality(context: Context): Boolean {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getBoolean(KEY_WEBCAM_QUALITY, true)
    }

}