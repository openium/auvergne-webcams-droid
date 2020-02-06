package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics


/**
 * Created by Openium on 19/02/2019.
 */
object FirebaseUtils {

    // TODO move all that's behind in PreferencesUtils

    // --- Refresh user properties variables
    private const val KEY_REFRESH_ENABLED = "KEY_REFRESH_ENABLED"
    private const val KEY_REFRESH_ENABLED_ANALYTICS = "refresh"

    // --- Refresh interval user properties
    private const val KEY_REFRESH_INTERVAL_ENABLED = "KEY_REFRESH_INTERVAL_ENABLED"
    private const val KEY_REFRESH_INTERVAL_ENABLED_ANALYTICS = "refresh_interval"

    // --- Webcam quality user properties
    private const val KEY_WEBCAM_QUALITY_ENABLED = "KEY_WEBCAM_QUALITY_ENABLED"
    private const val KEY_WEBCAM_QUALITY_ENABLED_ANALYTICS = "webcam_quality"

    fun setUserPropertiesRefreshPreferences(context: Context, isEnabled: Boolean) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putBoolean(KEY_REFRESH_ENABLED, isEnabled).apply()
    }

    fun getUserPropertiesRefreshPreferences(context: Context): Boolean {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getBoolean(KEY_REFRESH_ENABLED, true)
    }

    fun setUserPropertiesRefreshIntervalPreferences(context: Context, interval: Int) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putInt(KEY_REFRESH_INTERVAL_ENABLED, interval).apply()
    }

    fun getUserPropertiesRefreshIntervalPreferences(context: Context): Int {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getInt(KEY_REFRESH_INTERVAL_ENABLED, 0)
    }

    fun setUserPropertiesWebcamQualityPreferences(context: Context, quality: String) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putString(KEY_WEBCAM_QUALITY_ENABLED, quality).apply()
    }

    fun getUserPropertiesWebcamQualityPreferences(context: Context): String {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getString(KEY_WEBCAM_QUALITY_ENABLED, "high") ?: "high" // TODO
    }

    // --- User properties
    // ----------------------------------

    fun sendFirebaseUserPropertiesRefreshPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(KEY_REFRESH_ENABLED_ANALYTICS, getUserPropertiesRefreshPreferences(context).toString())
    }

    fun sendFirebaseUserPropertiesRefreshIntervalPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(
            KEY_REFRESH_INTERVAL_ENABLED_ANALYTICS,
            getUserPropertiesRefreshIntervalPreferences(context).toString()
        )
    }

    fun sendFirebaseUserPropertiesWebcamQualityPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(KEY_WEBCAM_QUALITY_ENABLED_ANALYTICS, getUserPropertiesWebcamQualityPreferences(context))
    }

    // --- Other methods
    // ----------------------------------

    /**
     * Generic log event method
     */
    fun logContentEvent(
        firebaseAnalytics: FirebaseAnalytics,
        contentName: String,
        contentType: String? = null,
        value: String? = null
    ) {
        val params = Bundle()

        contentType?.let {
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }

        value?.let {
            params.putString(FirebaseAnalytics.Param.ITEM_ID, value)
        }

        firebaseAnalytics.logEvent(contentName, params)
    }

    /**
     * Generic log search event method
     */
    fun logSearchEvent(firebaseAnalytics: FirebaseAnalytics, value: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, value)
        })
    }

    /**
     * Generic log view item list event method
     */
    fun logViewItemListEvent(firebaseAnalytics: FirebaseAnalytics, value: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, value)
        })
    }
}