package fr.openium.auvergnewebcams.broadcast

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.utils.NotificationUtils

object AppNotifier {

    object SaveWebcamAction {

        fun downloadingFile(context: Context, webcamName: String, notifBaseId: Int, downloadProgress: Int) {
            val title = context.getString(R.string.save_progress_title)
            val description = context.getString(R.string.save_description, webcamName)
            sendNotification(context, notifBaseId, title, description, downloadProgress)
        }

        fun downloadSuccess(context: Context, webcamName: String, notifBaseId: Int, savedWebcam: Bitmap?) {
            // Cancel progress notification
            cancel(context, notifBaseId)

            val title = context.getString(R.string.save_success_title)
            val description = context.getString(R.string.save_description, webcamName)
            sendNotification(context, notifBaseId, title, description, image = savedWebcam)
        }

        fun downloadError(context: Context, webcamName: String, notifBaseId: Int) {
            // Cancel progress notification
            cancel(context, notifBaseId)

            val title = context.getString(R.string.save_error_title)
            val description = context.getString(R.string.save_error_description, webcamName)
            sendNotification(context, notifBaseId, title, description)
        }

        private fun cancel(context: Context, idNotif: Int) {
            NotificationManagerCompat.from(context).cancel(idNotif)
        }
    }

    private fun sendNotification(
        context: Context,
        idNotif: Int,
        title: String,
        description: String,
        progress: Int? = null,
        image: Bitmap? = null
    ) {
        // Create the notification
        val builder = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID).apply {
            setAutoCancel(true)
            setContentTitle(title)
            setContentText(description)
            setOnlyAlertOnce(true)
            setSmallIcon(R.mipmap.ic_notif)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                setGroup(null) // Unfortunately required to be display
            }

            priority = if (progress != null) {
                // Add progress only if it's download notification
                setProgress(100, progress, false)
                NotificationCompat.PRIORITY_LOW
            } else {
                NotificationCompat.PRIORITY_DEFAULT
            }

            image?.let {
                setLargeIcon(it)
                setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(it)
                        .bigLargeIcon(null)
                )
            } ?: setStyle(NotificationCompat.BigTextStyle().bigText(description))
        }

        // Init channels before notify
        NotificationUtils.initChannels(context)

        // Notify
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(idNotif, builder.build())
    }
}
