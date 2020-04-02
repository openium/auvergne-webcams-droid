package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.kotlintools.ext.*

/**
 * Created by Openium on 19/02/2019.
 */
class PreferencesUtils(val context: Context) {

    var webcamsDelayRefreshValue: Int
        get() = context.getIntPref(KEY_WEBCAM_DELAY_REFRESH_VALUE, DEFAULT_REFRESH_DELAY)
        set(accepted) = context.setIntPref(KEY_WEBCAM_DELAY_REFRESH_VALUE, accepted)

    var isWebcamsDelayRefreshActive: Boolean
        get() = context.getBooleanPref(KEY_WEBCAM_DELAY_REFRESH, true)
        set(accepted) = context.setBooleanPref(KEY_WEBCAM_DELAY_REFRESH, accepted)

    var isWebcamsHighQuality: Boolean
        get() = context.getBooleanPref(KEY_WEBCAM_QUALITY, true)
        set(accepted) = context.setBooleanPref(KEY_WEBCAM_QUALITY, accepted)

    // TODO
    var lastUpdateWebcamsTimestamp: Long
        get() {
            var lastUpdate = context.getLongPref(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, -1)
            if (isWebcamsDelayRefreshActive) {
                if (lastUpdate == -1L) {
                    lastUpdate = System.currentTimeMillis().toUnixTimestamp()
                    lastUpdateWebcamsTimestamp = lastUpdate
                } else {
                    val delayRefreshInSec = webcamsDelayRefreshValue * 60
                    val actualTimestamp = System.currentTimeMillis().toUnixTimestamp()
                    val diff = actualTimestamp - lastUpdate
                    if (diff > delayRefreshInSec) {
                        lastUpdate = actualTimestamp
                        lastUpdateWebcamsTimestamp = lastUpdate
                    }
                }
            }
            return lastUpdate
        }
        set(value) = context.setLongPref(KEY_WEBCAM_LAST_UPDATE_TIMESTAMP, value)

    var newNotifId: Int
        get() {
            val value = context.getIntPref(KEY_NOTIF_ID, 0)
            newNotifId = value + 1
            return value
        }
        set(accepted) = context.setIntPref(KEY_NOTIF_ID, accepted)

    companion object {
        const val DEFAULT_REFRESH_DELAY = 10

        // Common
        private const val KEY_WEBCAM_QUALITY = "KEY_WEBCAM_QUALITY"
        private const val KEY_WEBCAM_DELAY_REFRESH = "KEY_WEBCAM_DELAY_REFRESH"
        private const val KEY_WEBCAM_DELAY_REFRESH_VALUE = "KEY_WEBCAM_DELAY_REFRESH_VALUE"
        private const val KEY_WEBCAM_LAST_UPDATE_TIMESTAMP = "KEY_WEBCAM_LAST_UPDATE_TIMESTAMP"
        private const val KEY_NOTIF_ID = "KEY_NOTIF_ID"
    }
}