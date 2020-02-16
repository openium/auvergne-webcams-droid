package fr.openium.auvergnewebcams.service

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.broadcast.AppNotifier
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL


class DownloadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams), KodeinAware {

    private var mUrl: String? = null
    private var mFileName: String = ""
    private var webcamName: String = ""

    private var mIsPhoto: Boolean = true
    private var mUri: Uri? = null

    override val kodein: Kodein by closestKodein(applicationContext)
    private val preferencesUtils: PreferencesUtils by instance()

    override fun doWork(): Result {
        mUrl = inputData.getString(KEY_PATH_URL)
        mIsPhoto = inputData.getBoolean(KEY_IS_PHOTO, true)
        mFileName = inputData.getString(KEY_FILENAME) ?: ""
        webcamName = mFileName.split("_").first().replace("\n", " - ")

        return downloadFile()
    }

    private fun downloadFile(): Result {
        val notifBaseId = preferencesUtils.newNotifId

        Timber.d("[Worker] Start download notification n°$notifBaseId")
        var result = Result.success()

        if (!mUrl.isNullOrEmpty()) {

            val oS = getOutputStream()

            val connection = URL(mUrl).openConnection()
            connection.connect()

            val contentLength = connection.contentLength
            if (contentLength > 0) {
                try {
                    val iS = connection.getInputStream()

                    val buffer = ByteArray(contentLength)
                    var total = 0

                    var currentProgress = 0
                    var count = iS.read(buffer)

                    while (count != -1) {
                        total += count
                        val progress = (total * 100) / contentLength
                        if (currentProgress != progress) {
                            Timber.d("[Worker] Downloading $currentProgress%")
                            AppNotifier.SaveWebcamAction.downloadingFile(applicationContext, webcamName, notifBaseId, progress)
                            currentProgress = progress
                        }
                        oS.write(buffer, 0, count)
                        count = iS.read(buffer)
                    }

                    // Create the bitmap
                    var bitmap: Bitmap? = null
                    if (mIsPhoto && mUri != null) {
                        applicationContext.contentResolver.openInputStream(mUri!!)?.let {
                            bitmap = BitmapFactory.decodeStream(it)
                            it.close()
                        }
                    }

                    oS.flush()
                    oS.close()
                    iS.close()

                    SystemClock.sleep(100)

                    Timber.d("[Worker] Finish download notification n°$notifBaseId with SUCCESS")
                    AppNotifier.SaveWebcamAction.downloadSuccess(
                        applicationContext,
                        webcamName,
                        notifBaseId,
                        bitmap,
                        mUri
                    )
                } catch (e: Exception) {
                    Timber.e(e)

                    Timber.d("[Worker] Finish download notification n°$notifBaseId with ERROR")
                    AppNotifier.SaveWebcamAction.downloadError(applicationContext, webcamName, notifBaseId)
                    result = Result.failure()
                }
            }
        } else {
            AppNotifier.SaveWebcamAction.downloadError(applicationContext, webcamName, notifBaseId)
            result = Result.failure()
        }

        return result
    }

    private fun getOutputStream(): OutputStream {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getOutPutStreamForApiAboveQ() ?: getOutputStreamForApiUnderQ()
        } else {
            getOutputStreamForApiUnderQ()
        }
    }

    private fun getOutPutStreamForApiAboveQ(): OutputStream? {
        val relativePath =
            if (mIsPhoto) {
                Environment.DIRECTORY_PICTURES
            } else {
                Environment.DIRECTORY_MOVIES
            } + File.separator + applicationContext.getString(R.string.app_name)

        val resolver = applicationContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, mFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, if (mIsPhoto) "image/*" else "video/*")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }

        val baseUri = if (mIsPhoto) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        
        return resolver.insert(baseUri, contentValues)?.let {
            mUri = it
            resolver.openOutputStream(it)
        }
    }

    private fun getOutputStreamForApiUnderQ(): FileOutputStream {
        val directory = if (mIsPhoto) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        }

        directory.mkdirs()

        val file = File(directory, mFileName)
        mUri = file.toUri()

        return FileOutputStream(file)
    }

    companion object {
        const val KEY_PATH_URL = "KEY_PATH_URL"
        const val KEY_IS_PHOTO = "KEY_IS_PHOTO"
        const val KEY_FILENAME = "KEY_FILENAME"
    }
}