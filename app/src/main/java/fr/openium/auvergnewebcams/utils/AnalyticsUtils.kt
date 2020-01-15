package fr.openium.auvergnewebcams.utils

import android.content.Context
import com.crashlytics.android.answers.Answers
import com.google.firebase.analytics.FirebaseAnalytics


/**
 * Created by Openium on 19/02/2019.
 */
object AnalyticsUtils {

    // --- Content Type / Content Id
    private const val KEY_RATE_APP = "rate_app"
    private const val KEY_ABOUT = "about"
    private const val KEY_WEBSITE_PIRATES = "website_lespirates"
    private const val KEY_WEBSITE_OPENIUM = "website_openium"
    private const val KEY_HOME_REFRESH = "home_refresh"
    private const val KEY_SEARCH = "search"
    private const val KEY_SETTINGS = "settings"
    private const val KEY_WEBCAM_DETAIL_REFRESH = "webcam_detail_refresh"
    private const val KEY_REPORT_WEBCAM_ERROR = "report_webcam_error"
    private const val KEY_SAVE_WEBCAM = "save_webcam"
    private const val KEY_SHARE_WEBCAM = "share"
    private const val KEY_BUTTON = "button"

    // --- Event Type
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM = "webcam"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE = "favorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM = "propose_webcam"

    // --- Favorite values ---
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE = "favorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE = "unfavorite"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE_FROM_SECTION = "favorite_from_section"
    private const val KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE_FROM_SECTION = "unfavorite_from_section"

    /***
     * UserProperties :
     * Propriété de refresh des webcams
     * Propriété du délai de refresh des webcams
     * Propriété de la qualité des webcams
     */
    fun sendAllUserProperties(context: Context) {
        FirebaseUtils.sendFirebaseUserPropertiesRefreshPreferences(context, FirebaseAnalytics.getInstance(context))
        FirebaseUtils.sendFirebaseUserPropertiesRefreshIntervalPreferences(context, FirebaseAnalytics.getInstance(context))
        FirebaseUtils.sendFirebaseUserPropertiesWebcamQualityPreferences(context, FirebaseAnalytics.getInstance(context))
    }

    /***
     * Ouverture de l'application
     */
    fun appIsOpen(context: Context) {
        FirebaseUtils.logContentEvent(FirebaseAnalytics.getInstance(context), FirebaseAnalytics.Event.APP_OPEN)
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.APP_OPEN)
    }

    /***
     * Clic sur la barre de recherche
     */
    fun searchStarted(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_SEARCH
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_SEARCH)
    }

    /***
     * Pull-to-refresh sur l'écran
     */
    fun homeRefreshed(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_HOME_REFRESH
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_HOME_REFRESH)
    }

    /***
     * Clic sur le bouton d'accès aux paramètres
     */
    fun settingsClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_SETTINGS
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_SETTINGS)
    }

    /***
     * Clic sur une webcam pour accéder à son détail
     */
    fun webcamDetailsClicked(context: Context, webcamTitle: String) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM,
            webcamTitle
        )
        FabricUtils.logContentViewEvent(
            Answers.getInstance(),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_WEBCAM,
            webcamTitle
        )
    }

    /***
     * Clic sur une section pour accéder à la liste de toutes les webcams de cette section
     */
    fun sectionDetailsClicked(context: Context, sectionTitle: String) {
        FirebaseUtils.logViewItemListEvent(FirebaseAnalytics.getInstance(context), sectionTitle)
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.VIEW_ITEM_LIST, sectionTitle)
    }

    /***
     * Refresh de la webcam sur son détail
     */
    fun webcamDetailRefreshed(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_WEBCAM_DETAIL_REFRESH
        )
        FabricUtils.logContentViewEvent(
            Answers.getInstance(),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_WEBCAM_DETAIL_REFRESH
        )
    }

    /***
     * Clic sur le bouton de partage d'une webcam
     */
    fun shareWebcamClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_SHARE_WEBCAM
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_SHARE_WEBCAM)
    }

    /***
     * Clic sur le bouton d'enregistrement d'une webcam
     */
    fun saveWebcamClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_SAVE_WEBCAM
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_SAVE_WEBCAM)
    }

    /***
     * Clic sur le bouton de signalement d'une webcam
     */
    fun signalProblemClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_REPORT_WEBCAM_ERROR
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_REPORT_WEBCAM_ERROR)
    }

    /***
     * Clic sur le bouton favoris d'une webcam
     */
    fun favoriteClicked(context: Context, webcamTitle: String, becameFavorite: Boolean) {
        val favStr = if (becameFavorite) KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE else KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE

        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE,
            favStr,
            webcamTitle
        )
        FabricUtils.logContentViewEvent(
            Answers.getInstance(),
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE,
            favStr,
            webcamTitle
        )
    }

    /***
     * Lorsqu'une recherche a été effectué
     */
    fun searchRequestDone(context: Context, searchText: String) {
        FirebaseUtils.logSearchEvent(FirebaseAnalytics.getInstance(context), searchText)
        FabricUtils.logSearchEvent(Answers.getInstance(), searchText)
    }

    /***
     * Clic sur le bouton "A propos"
     */
    fun aboutClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_ABOUT
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_ABOUT)
    }

    /***
     * Clic sur le bouton d'accès au site Openium
     */
    fun websiteOpeniumClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_WEBSITE_OPENIUM
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_WEBSITE_OPENIUM)
    }

    /***
     * Clic sur le bouton d'accès au site Les Pirates
     */
    fun lesPiratesClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_WEBSITE_PIRATES
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_WEBSITE_PIRATES)
    }

    /***
     * Clic sur le bouton pour noter l'application
     */
    fun rateAppClicked(context: Context) {
        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            FirebaseAnalytics.Event.SELECT_CONTENT,
            KEY_BUTTON,
            KEY_RATE_APP
        )
        FabricUtils.logContentViewEvent(Answers.getInstance(), FirebaseAnalytics.Event.SELECT_CONTENT, KEY_BUTTON, KEY_RATE_APP)
    }

    /***
     * Clic sur le bouton de proposition d'une nouvelle webcam
     */
    fun suggestWebcamClicked(context: Context) {
        FirebaseUtils.logContentEvent(FirebaseAnalytics.getInstance(context), KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM)
        FabricUtils.logContentViewEvent(Answers.getInstance(), KEY_SPECIAL_EVENTS_CONTENT_TYPE_PROPOSE_WEBCAM)
    }

    /***
     * Clic sur le bouton pour mettre en favoris une webcam depuis le détail d'une section
     */
    fun favoriteFromSectionClicked(context: Context, webcamTitle: String, becameFavorite: Boolean) {
        val favStr = if (becameFavorite) {
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_FAVORITE_FROM_SECTION
        } else {
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_IS_NOT_FAVORITE_FROM_SECTION
        }

        FirebaseUtils.logContentEvent(
            FirebaseAnalytics.getInstance(context),
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE,
            favStr,
            webcamTitle
        )
        FabricUtils.logContentViewEvent(
            Answers.getInstance(),
            KEY_SPECIAL_EVENTS_CONTENT_TYPE_FAVORITE,
            favStr,
            webcamTitle
        )
    }
}