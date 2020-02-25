package fr.openium.auvergnewebcams.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import fr.openium.auvergnewebcams.R

object NotificationUtils {

    // It's safe to call this repeatedly because creating an existing notification channel performs no operation.
    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val appChannel = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(appChannel)
        }
    }

    const val CHANNEL_ID = "AUVERGNE_WEBCAM"
}