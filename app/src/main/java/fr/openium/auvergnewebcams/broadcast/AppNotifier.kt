package fr.openium.auvergnewebcams.broadcast

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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

        fun downloadSuccess(context: Context, webcamName: String, notifBaseId: Int, savedWebcam: Bitmap?, savedFileUri: Uri?) {
            // Cancel progress notification
            cancel(context, notifBaseId)

            val title = context.getString(R.string.save_success_title)
            val description = context.getString(R.string.save_description, webcamName)
            sendNotification(context, notifBaseId, title, description, image = savedWebcam, fileUri = savedFileUri)
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
        image: Bitmap? = null,
        fileUri: Uri? = null
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

            fileUri?.let {
                val galleryIntent = Intent(Intent.ACTION_VIEW, fileUri)
                val contentIntent = PendingIntent.getActivity(
                    context.applicationContext,
                    0,
                    galleryIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                setContentIntent(contentIntent)
            }
        }

        // Init channels before notify
        NotificationUtils.initChannels(context)

        // Notify
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(idNotif, builder.build())
    }
}
