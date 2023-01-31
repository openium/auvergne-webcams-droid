package fr.openium.auvergnewebcams.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.decode.DataSource
import coil.request.Disposable
import coil.request.ImageRequest
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.metadata.ImageInfoExtractor
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Created by Piasy{github.com/Piasy} on 09/11/2016.
 */
class CoilImageLoader(var context: Context, imageLoader: coil.ImageLoader) : ImageLoader {

    private val mRequestDisposableMap = hashMapOf<Int, Disposable>()
    private val mImageLoader = imageLoader

    override fun loadImage(requestId: Int, uri: Uri, callback: ImageLoader.Callback) {
        val file = File(context.externalCacheDir.toString(), "latestImageDownloaded.jpg")

        var isCacheHit = false

        val disposable = mImageLoader.enqueue(
            ImageRequest.Builder(context)
                .data(uri)
                .listener(
                    onSuccess = { _, result ->
                        isCacheHit = result.dataSource == DataSource.DISK || result.dataSource == DataSource.MEMORY
                    }
                )
                .target(
                    onStart = { callback.onStart() },
                    onSuccess = { result ->
                        if (result is BitmapDrawable) {
                            saveBitmapToFile(file, result.bitmap, CompressFormat.JPEG, 100)
                        }

                        Timber.d("TEST isCacheHit: $isCacheHit")

                        if (isCacheHit) {
                            callback.onCacheHit(ImageInfoExtractor.getImageType(file), file)
                        } else callback.onCacheMiss(ImageInfoExtractor.getImageType(file), file)

                        callback.onSuccess(file)
                    },
                    onError = {
                        callback.onFail(Exception("Error on loading image with Coil"))
                    }
                )
                .build()
        )

        saveTarget(requestId, disposable)
    }

    private fun saveBitmapToFile(imageFile: File, bitmap: Bitmap, format: CompressFormat?, quality: Int) {
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(format, quality, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Timber.e(e, "Error writing bitmap")
        }
    }

    override fun prefetch(uri: Uri) {
        mImageLoader.enqueue(ImageRequest.Builder(context).data(uri).build())
    }

    override fun cancel(requestId: Int) {
        clearTarget(requestId)
    }

    private fun clearTarget(requestId: Int) {
        mRequestDisposableMap.remove(requestId)?.dispose()
    }

    override fun cancelAll() {
        for (key in mRequestDisposableMap.keys) {
            cancel(key)
        }
    }

    private fun saveTarget(requestId: Int, disposable: Disposable) {
        mRequestDisposableMap[requestId] = disposable
    }
}