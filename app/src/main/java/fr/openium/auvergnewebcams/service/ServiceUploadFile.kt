package fr.openium.auvergnewebcams.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import fr.openium.auvergnewebcams.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Created by laura on 07/12/2017.
 */
class ServiceUploadFile : Service() {

    companion object {

        private const val ARG_PATH_URL = "ARG_PATH_URL"
        private const val ARG_IS_PHOTO = "ARG_IS_PHOTO"
        private const val ARG_FILENAME = "ARG_FILENAME"

        private const val NOTIF_ID = 1
        private const val NOTIF_CHANNEL = "auvergne_webcams"


        private const val ACTION_RETRY = "retry_upload"

        fun startServiceUploadFile(context: Context, url: String, isPhoto: Boolean, fileName: String) {
            val intent = Intent(context, ServiceUploadFile::class.java).apply {
                putExtra(ARG_PATH_URL, url)
                putExtra(ARG_IS_PHOTO, isPhoto)
                putExtra(ARG_FILENAME, fileName)
            }
            context.startService(intent)
        }
    }

    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    private var mUrl: String? = null
    private var mFileName: String? = null
    private var mIsPhoto: Boolean = true

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            mUrl = intent.getStringExtra(ARG_PATH_URL)
            mIsPhoto = intent.getBooleanExtra(ARG_IS_PHOTO, true)
            mFileName = intent.getStringExtra(ARG_FILENAME)
        }
        uploadFile()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        mNotificationManager?.cancel(NOTIF_ID)
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun uploadFile() {
        if (!mUrl.isNullOrEmpty()) {

            createNotification()

            Thread(Runnable {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/auvergne_webcams/")
                directory.mkdirs()

                val url = URL(mUrl)
                val connection = url.openConnection()
                connection.connect()
                val contentLength = connection.contentLength

                if (contentLength > 0) {
                    val texte: String
                    if (mIsPhoto) {
                        texte = getString(R.string.detail_save_image_progress)
                    } else {
                        texte = getString(R.string.detail_save_video_progress)
                    }

                    try {
                        val inputStream = connection.getInputStream()
                        val file = File(directory, mFileName)
                        val outputStream = FileOutputStream(file)

                        val buffer = ByteArray(contentLength)
                        var total = 0

                        var currentProgress = 0
                        var count = inputStream.read(buffer)
                        while (count != -1) {
                            total += count
                            val progress = (total * 100) / contentLength
                            if (currentProgress != progress) {
                                // Timber.e("PROGRESS = ${progress}")
                                notifyProgress(100, progress, texte)
                                currentProgress = progress
                            }
                            outputStream.write(buffer, 0, count)
                            count = inputStream.read(buffer)
                        }
                        outputStream.flush()
                        outputStream.close()
                        inputStream.close()

                        val textSuccess: String
                        if (mIsPhoto) {
                            textSuccess = getString(R.string.detail_save_image_success)
                        } else {
                            textSuccess = getString(R.string.detail_save_video_success)
                        }
                        initNotifEnd(textSuccess, false)
                    } catch (error: Exception) {
                        Timber.e(error)
                        val texteError: String
                        if (mIsPhoto) {
                            texteError = getString(R.string.detail_save_image_error)
                        } else {
                            texteError = getString(R.string.detail_save_video_error)
                        }

                        initNotifEnd(texteError, true)
                    }
                }
            }).start()
        }
    }

    private fun createNotification() {
        val texte: String
        if (mIsPhoto) {
            texte = getString(R.string.detail_save_image_progress)
        } else {
            texte = getString(R.string.detail_save_video_progress)
        }

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager!!.createNotificationChannel(NotificationChannel(NOTIF_CHANNEL, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT))
        }
        mBuilder = NotificationCompat.Builder(this, NOTIF_CHANNEL)
                .setTicker(texte)
                .setSmallIcon(R.mipmap.ic_notif)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(getString(R.string.app_name)))
                .setOngoing(false)
        notifyProgress(0, 0, texte)
    }


    private fun notifyProgress(total: Int, progress: Int, text: String?) {
        mBuilder?.setProgress(total, progress, false)
        if (text != null) {
            mBuilder?.setStyle(NotificationCompat.BigTextStyle()
                    .setBigContentTitle(getString(R.string.app_name))
                    .bigText(text))
        }
        mNotificationManager?.notify(NOTIF_ID, mBuilder?.build())
    }

    private fun initNotifEnd(text: String, addRetry: Boolean) {
        //   createNotification()
        mNotificationManager?.cancel(NOTIF_ID)
        if (addRetry) {
            addActionRetry()
        }
        //  mBuilder?.setOngoing(false)
        //     mBuilder?.setSmallIcon(android.R.drawable.stat_sys_upload_done)
        notifyProgress(0, 0, text)
    }

    private fun addActionRetry() {
        val intent = Intent()
        intent.action = ACTION_RETRY

        val filter = IntentFilter()
        filter.addAction(ACTION_RETRY)
        registerReceiver(mReceiver, filter)

        val pendingIntent = PendingIntent.getBroadcast(this, 10, intent, 0)
        mBuilder?.addAction(R.drawable.ic_refresh, getString(R.string.detail_save_file_retry), pendingIntent)
    }

    // =================================================================================================================
    // Receiver
    // =================================================================================================================

    internal var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_RETRY) {
                mNotificationManager?.cancel(NOTIF_ID)
                uploadFile()
            }
        }
    }

}