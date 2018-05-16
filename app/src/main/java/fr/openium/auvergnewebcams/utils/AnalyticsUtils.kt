package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.SearchEvent
import com.google.firebase.analytics.FirebaseAnalytics


/**
 * Created by laura on 04/01/2018.
 */
object AnalyticsUtils {

    // ======================================= //
    // USER PROPERTIES
    // ======================================= //

    // ========= VARIABLES =========

    // --- Refresh user properties variables ---
    private const val KEY_REFRESH_ENABLED = "KEY_REFRESH_ENABLED"
    private const val KEY_REFRESH_ENABLED_ANALYTICS = "refresh"

    // --- Refresh interval user properties part ---
    private const val KEY_REFRESH_INTERVAL_ENABLED = "KEY_REFRESH_INTERVAL_ENABLED"
    private const val KEY_REFRESH_INTERVAL_ENABLED_ANALYTICS = "refresh_interval"

    // --- Webcam quality user properties part ---
    private const val KEY_WEBCAM_QUALITY_ENABLED = "KEY_WEBCAM_QUALITY_ENABLED"
    private const val KEY_WEBCAM_QUALITY_ENABLED_ANALYTICS = "webcam_quality"

    // ========= COMMON METHODS =========

    // --- Refresh user properties part ---
    fun setUserPropertiesRefreshPreferences(context: Context, isEnabled: Boolean) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putBoolean(KEY_REFRESH_ENABLED, isEnabled).apply()
    }

    fun getUserPropertiesRefreshPreferences(context: Context): Boolean {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getBoolean(KEY_REFRESH_ENABLED, true)
    }

    // --- Refresh interval properties part ---
    fun setUserPropertiesRefreshIntervalPreferences(context: Context, interval: Int) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putInt(KEY_REFRESH_INTERVAL_ENABLED, interval).apply()
    }

    fun getUserPropertiesRefreshIntervalPreferences(context: Context): Int {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getInt(KEY_REFRESH_INTERVAL_ENABLED, 0)
    }

    // --- Webcam quality properties part ---
    fun setUserPropertiesWebcamQualityPreferences(context: Context, quality: String) {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesManager.edit().putString(KEY_WEBCAM_QUALITY_ENABLED, quality).apply()
    }

    fun getUserPropertiesWebcamQualityPreferences(context: Context): String {
        val preferencesManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferencesManager.getString(KEY_WEBCAM_QUALITY_ENABLED, "high")
    }

    // ========= METHODS =========

    // --- Refresh user properties part ---
    private fun sendFirebaseUserPropertiesRefreshPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(KEY_REFRESH_ENABLED_ANALYTICS, getUserPropertiesRefreshPreferences(context).toString())
    }

    // --- Refresh interval properties part ---
    private fun sendFirebaseUserPropertiesRefreshIntervalPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(KEY_REFRESH_INTERVAL_ENABLED_ANALYTICS, getUserPropertiesRefreshIntervalPreferences(context).toString())
    }

    // --- Webcam quality properties part ---
    private fun sendFirebaseUserPropertiesWebcamQualityPreferences(context: Context, firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.setUserProperty(KEY_WEBCAM_QUALITY_ENABLED_ANALYTICS, getUserPropertiesWebcamQualityPreferences(context))
    }

    // ======================================= //
    // LOG SPECIALS EVENTS
    // ======================================= //

    // ========= VARIABLES =========

    // --- View item list part ---
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM = "webcam"

    // --- Favorite part ---
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE = "favorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE = "favorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE = "unfavorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM = "propose_webcam"

    // ========= FIREBASE METHODS =========

    // --- Ouverture de l'application ---
    private fun sendFirebaseLogEventAppOpen(firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    // --- Recherche dans la liste des webcams ---
    private fun sendFirebaseLogEventSearch(firebaseAnalytics: FirebaseAnalytics, text: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, text)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    // --- Affichage d'une section (liste des webcams d'un domaine. Par exemple "Puy de Sancy") ---
    private fun sendFirebaseLogEventViewItemList(firebaseAnalytics: FirebaseAnalytics, sectionName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, sectionName)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle)
    }

    // --- Affichage du détail d'une webcam ---
    private fun sendFirebaseLogEventSelectDetail(firebaseAnalytics: FirebaseAnalytics, webcamName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM)
            putString(FirebaseAnalytics.Param.ITEM_ID, webcamName)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    // --- Ajout/Suppression d'une webcam aux favoris ---
    private fun sendFirebaseLogEventFavorite(firebaseAnalytics: FirebaseAnalytics, webcamName: String, isBecomeFavorite: Boolean) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, if (isBecomeFavorite) {
                KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE
            } else {
                KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE
            })
            putString(FirebaseAnalytics.Param.ITEM_ID, webcamName)
        }

        firebaseAnalytics.logEvent(KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE, bundle)
    }

    // --- Proposition d'une nouvelle webcam  ---
    private fun sendFirebaseLogEventNewWebcam(firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.logEvent(KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM, null)
    }

    // ========= FABRIC METHODS =========

    // --- Ouverture de l'application ---
    private fun sendFabricLogEventAppOpen(answersAnalytics: Answers) {
        answersAnalytics.logContentView(ContentViewEvent().putContentName(FirebaseAnalytics.Event.APP_OPEN))
    }

    // --- Recherche dans la liste des webcams ---
    private fun sendFabricLogEventSearch(answersAnalytics: Answers, text: String) {
        answersAnalytics.logSearch(SearchEvent().putQuery(text))
    }

    // --- Affichage d'une section (liste des webcams d'un domaine. Par exemple "Puy de Sancy") ---
    private fun sendFabricLogEventViewItemList(answersAnalytics: Answers, sectionName: String) {
        answersAnalytics.logContentView(ContentViewEvent().putContentName(FirebaseAnalytics.Event.VIEW_ITEM_LIST).putContentId(sectionName))
    }

    // --- Affichage du détail d'une webcam ---
    private fun sendFabricLogEventSelectDetail(answersAnalytics: Answers, webcamName: String) {
        answersAnalytics.logContentView(ContentViewEvent()
                .putContentName(FirebaseAnalytics.Event.SELECT_CONTENT)
                .putContentType(KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM)
                .putContentId(webcamName))
    }

    // --- Ajout/Suppression d'une webcam aux favoris ---
    private fun sendFabricLogEventFavorite(answersAnalytics: Answers, webcamName: String, isBecomeFavorite: Boolean) {
        answersAnalytics.logContentView(ContentViewEvent()
                .putContentName(KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE)
                .putContentType(if (isBecomeFavorite) {
                    KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE
                } else {
                    KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE
                })
                .putContentId(webcamName))
    }

    // --- Proposition d'une nouvelle webcam  ---
    private fun sendFabricLogEventNewWebcam(answersAnalytics: Answers) {
        answersAnalytics.logContentView(ContentViewEvent().putContentName(KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM))
    }

    // ======================================= //
    // BUTTONS
    // ======================================= //

    // ========= VARIABLES =========

    // --- Content type ---
    private const val KEY_BUTTON = "button"

    // --- Rate App ---
    private const val KEY_BUTTON_RATE_APP = "rate_app"

    // --- About ---
    private const val KEY_BUTTON_ABOUT = "about"

    // --- Website Les Pirates ---
    private const val KEY_BUTTON_WEBSITE_PIRATES = "website_lespirates"

    // --- Website Openium ---
    private const val KEY_BUTTON_WEBSITE_OPENIUM = "website_openium"

    // --- Home Refresh ---
    private const val KEY_BUTTON_HOME_REFRESH = "home_refresh"

    // --- Search ---
    private const val KEY_BUTTON_SEARCH = "search"

    // --- Settings ---
    private const val KEY_BUTTON_SETTINGS = "settings"

    // --- Webcam Detail Refresh ---
    private const val KEY_BUTTON_WEBCAM_DETAIL_REFRESH = "webcam_detail_refresh"

    // --- Report Webcam Error ---
    private const val KEY_BUTTON_REPORT_WEBCAM_ERROR = "report_webcam_error"

    // --- Save Webcam ---
    private const val KEY_BUTTON_SAVE_WEBCAM = "save_webcam"

    // --- Share Webcam ---
    private const val KEY_BUTTON_SHARE_WEBCAM = "share"

    // ========= FIREBASE METHODS =========

    // --- Generic method to log all events not listed before ---
    private fun sendFirebaseLogEventSelectContent(firebaseAnalytics: FirebaseAnalytics, itemSelected: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, KEY_BUTTON)
            putString(FirebaseAnalytics.Param.ITEM_ID, itemSelected)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    // ========= FABRIC METHODS =========

    // --- Generic method to log all events not listed before ---
    private fun sendFabricLogEventSelectContent(answersAnalytics: Answers, itemSelected: String) {
        answersAnalytics.logContentView(ContentViewEvent()
                .putContentName(FirebaseAnalytics.Event.SELECT_CONTENT)
                .putContentType(KEY_BUTTON)
                .putContentId(itemSelected))
    }

    // ======================================= //
    // ALL PUBLIC METHODS YOU NEED TO CALL
    // ======================================= //

    fun appIsOpen(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventAppOpen(FirebaseAnalytics.getInstance(context))
        AnalyticsUtils.sendFabricLogEventAppOpen(Answers.getInstance())
    }

    fun sendAllUserPreferences(context: Context) {
        AnalyticsUtils.sendFirebaseUserPropertiesRefreshIntervalPreferences(context, FirebaseAnalytics.getInstance(context))
        AnalyticsUtils.sendFirebaseUserPropertiesRefreshPreferences(context, FirebaseAnalytics.getInstance(context))
        AnalyticsUtils.sendFirebaseUserPropertiesWebcamQualityPreferences(context, FirebaseAnalytics.getInstance(context))
    }

    fun buttonSearchClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_SEARCH)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_SEARCH)
    }

    fun buttonHomeRefreshed(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_HOME_REFRESH)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_HOME_REFRESH)
    }

    fun buttonSettingsClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_SETTINGS)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_SETTINGS)
    }

    fun selectWebcamDetails(context: Context, webcamTitle: String) {
        AnalyticsUtils.sendFirebaseLogEventSelectDetail(FirebaseAnalytics.getInstance(context), webcamTitle)
        AnalyticsUtils.sendFabricLogEventSelectDetail(Answers.getInstance(), webcamTitle)
    }

    fun selectSectionDetails(context: Context, sectionTitle: String) {
        AnalyticsUtils.sendFirebaseLogEventViewItemList(FirebaseAnalytics.getInstance(context), sectionTitle)
        AnalyticsUtils.sendFabricLogEventViewItemList(Answers.getInstance(), sectionTitle)
    }

    fun webcamDetailRefreshed(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_WEBCAM_DETAIL_REFRESH)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_WEBCAM_DETAIL_REFRESH)
    }

    fun buttonShareWebcamClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_SHARE_WEBCAM)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_SHARE_WEBCAM)
    }

    fun buttonSaveWebcamClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_SAVE_WEBCAM)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_SAVE_WEBCAM)
    }

    fun buttonSignalProblemClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_REPORT_WEBCAM_ERROR)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_REPORT_WEBCAM_ERROR)
    }

    fun buttonFavoriteClicked(context: Context, webcamTitle: String, isBecomeFavorite: Boolean) {
        AnalyticsUtils.sendFirebaseLogEventFavorite(FirebaseAnalytics.getInstance(context), webcamTitle, isBecomeFavorite)
        AnalyticsUtils.sendFabricLogEventFavorite(Answers.getInstance(), webcamTitle, isBecomeFavorite)
    }

    fun searchRequestDone(context: Context, searchText: String) {
        AnalyticsUtils.sendFirebaseLogEventSearch(FirebaseAnalytics.getInstance(context), searchText)
        AnalyticsUtils.sendFabricLogEventSearch(Answers.getInstance(), searchText)
    }

    fun buttonAboutClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_ABOUT)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_ABOUT)
    }

    fun buttonWebsiteOpeniumClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_WEBSITE_OPENIUM)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_WEBSITE_OPENIUM)
    }

    fun buttonLesPiratesClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_WEBSITE_PIRATES)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_WEBSITE_PIRATES)
    }

    fun buttonRateAppClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventSelectContent(FirebaseAnalytics.getInstance(context), AnalyticsUtils.KEY_BUTTON_RATE_APP)
        AnalyticsUtils.sendFabricLogEventSelectContent(Answers.getInstance(), AnalyticsUtils.KEY_BUTTON_RATE_APP)
    }

    fun buttonProposeWebcamClicked(context: Context) {
        AnalyticsUtils.sendFirebaseLogEventNewWebcam(FirebaseAnalytics.getInstance(context))
        AnalyticsUtils.sendFabricLogEventNewWebcam(Answers.getInstance())
    }

}